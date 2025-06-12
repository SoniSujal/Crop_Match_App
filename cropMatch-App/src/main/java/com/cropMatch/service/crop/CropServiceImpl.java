package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;

import com.cropMatch.exception.CategoryNotFoundException;
import com.cropMatch.exception.ImageInByterConvertException;
import com.cropMatch.model.admin.Category;
import com.cropMatch.model.farmer.Crop;
import com.cropMatch.model.farmer.CropImage;
import com.cropMatch.repository.category.CategoryRepository;
import com.cropMatch.repository.crop.CropImageRepository;
import com.cropMatch.repository.crop.CropRepository;
import com.cropMatch.service.category.CategoryService;
import com.cropMatch.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void saveCropWithImages(CropDTO cropDTO, List<MultipartFile> images, Integer farmerId) {

        Category category = categoryRepository.findById(cropDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found!"));

        Crop crop = new Crop(cropDTO,category);
        Crop cropData = cropRepository.save(crop);
        uploadFileWithData(farmerId,images, cropData.getId());

        try {
            for(MultipartFile image : images) {
                CropImage cropImage = new CropImage(cropData, image);
                cropImageRepository.save(cropImage);
            }
        } catch (IOException e) {
            throw new ImageInByterConvertException("Image Not Convert in Byter Formate");
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

    @Override
    public List<RecommendationDTO> recommedCropsDetailsBaseCategory(List<Integer> categoryIds) {
        if(CollectionUtils.isEmpty(categoryIds)) {
            return null;
        }
        return categoryIds.stream()
                .map(categoryId -> cropRepository.findCropsWithImagesByCategoryId(categoryId))
                .flatMap(List::stream)
                .map(RecommendationDTO::new)
                .collect(Collectors.toList());
    }
}
