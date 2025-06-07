package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.admin.Category;

import java.util.List;

public interface BuyerService {

    BuyerRequest createRequest(BuyerRequestDTO dto, String username);
    List<Category> getActiveCategories();
    List<BuyerServiceImpl.UnitDTO> getAllUnits();
    List<BuyerRequest> getAllRequests();

}
