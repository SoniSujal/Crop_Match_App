package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;

import com.cropMatch.dto.responseDTO.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

        Crop crop = new Crop(cropDTO, category);
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
    public PagedResponse<RecommendationDTO> recommedCropsDetailsBaseCategory(List<Integer> categoryIds, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // Fetch all matching + non-matching crops without Pageable (fetch all)
        List<Crop> matching = categoryIds.isEmpty() ? Collections.emptyList() : cropRepository.findByCategoryIdIn(categoryIds, sort);
        List<Crop> nonMatching = categoryIds.isEmpty() ? cropRepository.findAll(sort) : cropRepository.findByCategoryIdNotIn(categoryIds, sort);

        // Merge them: matching first
        List<Crop> combined = new ArrayList<>();
        combined.addAll(matching);
        combined.addAll(nonMatching);

        // Manual pagination
        int total = combined.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min(start + pageSize, total);
        List<Crop> pagedList = (start > total) ? new ArrayList<>() : combined.subList(start, end);

        List<RecommendationDTO> content = pagedList.stream()
                .map(crop -> new RecommendationDTO(crop, userService))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                pageNo,
                pageSize,
                total,
                (int) Math.ceil((double) total / pageSize),
                end >= total,
                pageNo == 0
        );
    }
}
