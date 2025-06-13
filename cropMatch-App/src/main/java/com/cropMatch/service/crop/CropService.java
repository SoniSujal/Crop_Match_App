package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CropService {

    void saveCropWithImages(@Valid CropDTO cropDTO, List<MultipartFile> images, Integer farmerId);
    public String uploadFileWithData(Integer farmerId,List<MultipartFile> file, Integer cropId);
    public List<RecommendationDTO> recommedCropsDetailsBaseCategory(List<Integer> categoryIds);

    List<RecommendationDTO> getTopRecommendations(String email);

}
