package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;
import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.responseDTO.PagedResponse;
import com.cropMatch.model.farmer.Crop;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface  CropService {

    public ApiResponse<RecommendationDTO> getCropById(Integer cropId);
    public void saveCropWithImages(CropDTO cropDTO, List<MultipartFile> images, Integer farmerId);
    public Page<Crop> recommedCropsDetailsBaseCategory(List<Integer> categoryIds, int pageNo, int pageSize, String sortBy, String sortDir);
    public Page<RecommendationDTO> getRecommendedCropsDTO(List<Integer> categoryIds, int pageNo, int pageSize, String sortBy, String sortDir);

}
