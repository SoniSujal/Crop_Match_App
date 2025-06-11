package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.admin.Category;

import java.util.List;

public interface BuyerService {

    BuyerRequest createRequest(BuyerRequestDTO dto, String username);
    List<Category> getActiveCategories();
    List<BuyerServiceImpl.UnitDTO> getAllUnits();
    List<BuyerRequestResponseDTO> getAllRequests(String username);
    boolean updateBuyerPreferences(String username, List<Integer> categoryIds);

    List<Integer> getBuyerPreferenceCategoryIds(Integer userId);
    List<Integer> getBuyerPreferences(String name);
}
