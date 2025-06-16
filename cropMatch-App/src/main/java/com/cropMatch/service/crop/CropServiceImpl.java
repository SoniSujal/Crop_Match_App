package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;

import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.exception.CategoryNotFoundException;
import com.cropMatch.exception.CropNotFoundException;
import com.cropMatch.exception.ImageInByterConvertException;
import com.cropMatch.model.admin.Category;
import com.cropMatch.model.farmer.Crop;
import com.cropMatch.model.farmer.CropImage;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerPreferencesRepository;
import com.cropMatch.repository.category.CategoryRepository;
import com.cropMatch.repository.crop.CropImageRepository;
import com.cropMatch.repository.crop.CropRepository;
import com.cropMatch.service.category.CategoryService;
import com.cropMatch.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
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

    @Autowired
    private BuyerPreferencesRepository buyerPreferencesRepository;

    @Override
    @Transactional
    public void saveCropWithImages(CropDTO cropDTO, List<MultipartFile> images, Integer farmerId) {

        Category category = categoryRepository.findById(cropDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found!"));

        Crop crop = new Crop(cropDTO, category,farmerId);
        Crop cropData = cropRepository.save(crop);

        String folderName = uploadPath + File.separator + "FARMER_ID_" + farmerId + File.separator + "CROP_ID_" + cropData.getId();
        File saveFile = new File(folderName);
        if(!saveFile.exists()) {
            saveFile.mkdirs();
        }

        try {
            for (MultipartFile image : images) {
                // Save file to folder
                Path filePath = Paths.get(folderName, image.getOriginalFilename());
                Files.copy(image.getInputStream(), filePath);

                // Construct relative image path for frontend use (example: /images/FARMER_ID_x/CROP_ID_y/image.jpg)
                String relativeImagePath = "/images/FARMER_ID_" + farmerId + "/CROP_ID_" + cropData.getId() + "/" + image.getOriginalFilename();

                // Save CropImage entity with imagePath (no need for imageData if using path)
                CropImage cropImage = new CropImage(cropData, image, relativeImagePath);
                cropImageRepository.save(cropImage);
            }
        } catch (IOException e) {
            throw new ImageInByterConvertException("Image Not Convert in Byte Format");
        }
    }


    @Override
    public Page<Crop> recommedCropsDetailsBaseCategory(List<Integer> categoryIds, int pageNo, int pageSize,  String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Crop> cropPage = cropRepository.findAllSortedByCategoryPreferenceNative(categoryIds, Pageable.unpaged());

        List<Integer> cropIds = cropPage.getContent().stream()
                .map(Crop::getId)
                .collect(Collectors.toList());

        List<Crop> cropsWithImages = cropIds.isEmpty()
                ? Collections.emptyList()
                : cropRepository.findAllWithImagesByIds(cropIds);

        Map<Integer, Crop> cropMap = cropsWithImages.stream()
                .collect(Collectors.toMap(Crop::getId, Function.identity()));

        // Step 3: Preserve category priority, then apply dynamic sorting
        List<Crop> sortedList = cropPage.stream()
                .map(crop -> cropMap.getOrDefault(crop.getId(), crop))
                .sorted(Comparator
                        .comparing((Crop c) -> categoryIds.contains(c.getCategory().getId()) ? 0 : 1)
                        .thenComparing(getComparator(sortBy, sortDir)))
                .collect(Collectors.toList());

        // Step 4: Manual pagination
        int total = sortedList.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min(start + pageSize, total);
        List<Crop> pagedList = sortedList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, total);
    }

    private Comparator<Crop> getComparator(String sortBy, String sortDir) {
        Comparator<Crop> comparator;

        switch (sortBy) {
            case "price" -> comparator = Comparator.comparing(Crop::getPrice);
            case "createdOn" -> comparator = Comparator.comparing(Crop::getCreatedOn);
            case "name" -> comparator = Comparator.comparing(Crop::getName, String.CASE_INSENSITIVE_ORDER);
            case "stockQuantity" -> comparator = Comparator.comparing(Crop::getStockQuantity);
            default -> comparator = Comparator.comparing(Crop::getCreatedOn); // fallback
        }

        return sortDir.equalsIgnoreCase("desc") ? comparator.reversed() : comparator;
    }

    @Override
    public Page<RecommendationDTO> getRecommendedCropsDTO(List<Integer> categoryIds, int pageNo, int pageSize, String sortBy, String sortDir) {
        Page<Crop> cropsPage = recommedCropsDetailsBaseCategory(categoryIds, pageNo, pageSize, sortBy, sortDir);
        Page<RecommendationDTO> map = cropsPage.map(crop -> new RecommendationDTO(crop, userService));
        return map;
    }

    @Override
    public ApiResponse<RecommendationDTO> getCropById(Integer cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException("Crop Not Found"));
        RecommendationDTO recommendationDTO = new RecommendationDTO(crop,userService);
        return ApiResponse.success(recommendationDTO);
    }

    @Override
    public List<RecommendationDTO> getTopRecommendations(String email){
        UserDetail buyer = userService.findByUserEmail(email);
        System.out.println("*****************************");
        System.out.println(buyer);
        List<Integer> categoryIds = buyerPreferencesRepository.findByBuyerId(buyer.getId())
                .stream()
                .map(pref -> pref.getCategory().getId())
                .toList();
        log.debug("****************************");
        log.debug(categoryIds.toString());
        List<Crop> crops = cropRepository.findTopRecommendations(categoryIds,buyer.getRegion());
        log.debug("***************************");
        log.debug(crops.toString());

        List<RecommendationDTO> list = crops.stream()
                .map(crop -> new RecommendationDTO(crop, userService))
                .toList();
        log.debug(list.toString());
        return list;
    }



}
