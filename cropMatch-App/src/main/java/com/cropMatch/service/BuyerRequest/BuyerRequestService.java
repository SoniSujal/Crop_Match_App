package com.cropMatch.service.BuyerRequest;

import com.cropMatch.dto.buyerDTO.FarmerRequestResponseDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.farmer.AvailableCrops;

import java.util.List;

public interface BuyerRequestService {

    List<BuyerRequest> getAllBuyerRequest();
    BuyerRequest saveRequest(BuyerRequest buyerRequest);
    AvailableCrops resolveClosestCropName(String inputCropName, Integer categoryId);
    void handleResponse(Integer requestId,String farmerUsername,String action);

    List<FarmerRequestResponseDTO> getAcceptedOrRejectedRequestsForBuyer(String buyerEmail);

}
