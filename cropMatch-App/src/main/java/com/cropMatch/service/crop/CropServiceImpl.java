package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;

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
                .orElseThrow(() -> new RuntimeException("Category Not Found!"));

        Crop crop = new Crop();
        crop.setName(cropDTO.getName());
        crop.setDescription(cropDTO.getDescription());
        crop.setCategory(category);
        crop.setQuantity(cropDTO.getQuantity());
        crop.setPrice(cropDTO.getPrice());
        crop.setStockUnit(cropDTO.getStockUnit());
        crop.setSellingUnit(cropDTO.getSellingUnit());
        crop.setCreatedBy(farmerId);
        crop.setStatus(true);
        crop.setRegion(cropDTO.getRegion());
        crop.setCreatedOn(LocalDateTime.now());
        crop.setUpdatedOn(LocalDateTime.now());
        crop.setExpireMonth(String.valueOf(cropDTO.getExpireMonth()));
        crop.setCropType(cropDTO.getCropType());
        crop.setQuality(cropDTO.getQuality());
        crop.setProducedWay(cropDTO.getProducedWay());
        crop.setAvailabilityStatus(cropDTO.getAvailabilityStatus());
        crop.setExpectedReadyMonth(String.valueOf(cropDTO.getExpectedReadyMonth()));
        Crop cropData = cropRepository.save(crop);

        uploadFileWithData(farmerId,images, cropData.getId());

        try {
            for(MultipartFile image : images) {
                CropImage cropImage = new CropImage();
                cropImage.setCrop(cropData);
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

    @Override
    public List<RecommendationDTO> recommedCropsDetailsBaseCategory(List<Integer> categoryIds) {
        if(CollectionUtils.isEmpty(categoryIds)) {
            return null;
        }
        List<RecommendationDTO> recommendationDTOList = new ArrayList<>();
        List<List<Crop>> recommedationList = categoryIds.stream()
                .map(categoryId -> cropRepository.findCropsWithImagesByCategoryId(categoryId)).toList();
        for(List<Crop> cropList : recommedationList) {
            for(Crop cropDetails : cropList) {
                RecommendationDTO recommendationDTO = new RecommendationDTO();
                recommendationDTO.setName(cropDetails.getName());
                recommendationDTO.setCategoryName(cropDetails.getCategory().getName());
                recommendationDTO.setQuantity(cropDetails.getQuantity());
                recommendationDTO.setPrice(cropDetails.getPrice());
                recommendationDTO.setStockUnit(cropDetails.getStockUnit());
                recommendationDTO.setSellingUnit(cropDetails.getSellingUnit());
                recommendationDTO.setRegion(cropDetails.getRegion());
                recommendationDTO.setExpireMonth(cropDetails.getExpireMonth());
                recommendationDTO.setCropType(cropDetails.getCropType());
                recommendationDTO.setQuality(cropDetails.getQuality());
                recommendationDTO.setProducedWay(cropDetails.getProducedWay());
                recommendationDTO.setAvailabilityStatus(cropDetails.getAvailabilityStatus());
                recommendationDTO.setImages(cropDetails.getImages());
                recommendationDTO.setExpectedReadyMonth(cropDetails.getExpectedReadyMonth());
                recommendationDTO.setSellerName(userService.findByUsernameUsingId(cropDetails.getCreatedBy()));
                recommendationDTOList.add(recommendationDTO);
            }
        }
        return recommendationDTOList;
    }
}
