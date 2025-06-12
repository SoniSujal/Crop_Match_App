package com.cropMatch.service.crop;

import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.farmerDTO.CropDTO;
import com.cropMatch.dto.responseDTO.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CropService {

    public void saveCropWithImages(CropDTO cropDTO, List<MultipartFile> images, Integer farmerId);
    public PagedResponse<RecommendationDTO> recommedCropsDetailsBaseCategory(List<Integer> categoryIds, int pageNo, int pageSize, String sortBy, String sortDir);

}
