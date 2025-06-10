package com.cropMatch.service.crop;

import com.cropMatch.dto.farmerDTO.CropDTO;

import com.cropMatch.model.admin.Category;
import com.cropMatch.model.farmer.Crop;
import com.cropMatch.model.farmer.CropImage;
import com.cropMatch.repository.category.CategoryRepository;
import com.cropMatch.repository.crop.CropImageRepository;
import com.cropMatch.repository.crop.CropRepository;
import com.cropMatch.service.category.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CropServiceImpl implements CropService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CropImageRepository cropImageRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveCropWithImages(CropDTO cropDTO, List<MultipartFile> images, Integer farmerId) {

        Category category = categoryRepository.findById(cropDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not Found!"));

        Crop crop = new Crop();
        crop.setName(cropDTO.getName());
        crop.setDescription(cropDTO.getDescription());
        crop.setCategory(category);
        crop.setQuantity(cropDTO.getQuantity());
        crop.setPrice(cropDTO.getPrice());
        crop.setUnit(cropDTO.getUnit());
        crop.setCreatedBy(farmerId);
        crop.setStatus(true);
        crop.setRegion(cropDTO.getRegion());
        crop.setCreatedOn(LocalDateTime.now());
        crop.setUpdatedOn(LocalDateTime.now());
        Crop saved = cropRepository.save(crop);

        uploadFileWithData(farmerId,images, saved.getId());

        try {
            for(MultipartFile image : images) {
                CropImage cropImage = new CropImage();
                cropImage.setCrop(saved);
                cropImage.setImageName(image.getOriginalFilename());
                cropImage.setImageType(image.getContentType());
                cropImage.setImageData(image.getBytes());
                cropImageRepository.save(cropImage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String uploadFileWithData(Integer farmerId,List<MultipartFile> images, Integer cropId) {

        String folderName = uploadPath + File.separator + "FARMER_ID_" + farmerId + File.separator + "CROP_ID_" + cropId;
        File saveFile = new File(folderName);
        if(!saveFile.exists()) {
            saveFile.mkdirs();
        }
        for (MultipartFile image : images) {
            try {
                Files.copy(image.getInputStream(), Paths.get(folderName, image.getOriginalFilename()));
            } catch (IOException e) {
                throw new RuntimeException("File Uploading Failed");
            }
        }
        return folderName;
    }
}
