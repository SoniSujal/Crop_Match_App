package com.cropMatch.service.user.buyer;

import com.cropMatch.dto.BuyerRequestDTO;
import com.cropMatch.model.BuyerRequest;
import com.cropMatch.model.Category;

import java.util.List;

public interface BuyerService {

    BuyerRequest createRequest(BuyerRequestDTO dto, String username);
    List<Category> getActiveCategories();
    List<BuyerServiceImpl.UnitDTO> getAllUnits();
    List<BuyerRequest> getAllRequests();

}
