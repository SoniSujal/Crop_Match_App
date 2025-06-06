package com.cropMatch.service.farmer;

import com.cropMatch.dto.CropDTO;
import com.cropMatch.enums.Category;
import com.cropMatch.model.Crop;
import com.cropMatch.model.CropImage;
import com.cropMatch.repository.CropImageRepository;
import com.cropMatch.repository.CropRepository;
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
public class FarmerServiceImpl implements FarmerService{

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CropImageRepository cropImageRepository;

    @Override
    @Transactional
    public void saveCropWithImages(CropDTO cropDTO, List<MultipartFile> images, Integer farmerId) {

        Crop crop = new Crop();
        crop.setName(cropDTO.getName());
        crop.setDescription(cropDTO.getDescription());
        crop.setCategory(Category.valueOf(cropDTO.getCategory()));
        crop.setQuantity(cropDTO.getQuantity());
        crop.setPrice(cropDTO.getPrice());
        crop.setUnit(cropDTO.getUnit());
        crop.setCreatedBy(farmerId);
        crop.setStatus(true);
        crop.setRegion(cropDTO.getRegion());
        crop.setCreatedOn(LocalDateTime.now());
        crop.setUpdatedOn(LocalDateTime.now());

        uploadFileWithData(farmerId,images);
        Crop saved = cropRepository.save(crop);


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
    public String uploadFileWithData(Integer farmerId,List<MultipartFile> images) {

        String folderName = uploadPath + File.separator + "FARMER_ID_" + farmerId;
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
