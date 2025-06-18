package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.dto.buyerDTO.CropMatchProjectionDTO;
import com.cropMatch.model.buyer.BuyerRequest;

import java.util.List;

public interface BuyerMatchingService {

    void SendToFarmers(List<CropMatchProjectionDTO> bestMatchingCrops, BuyerRequest request);

    List<BuyerRequestResponseDTO> getPendingRequestsForFarmer(String email);

    public List<CropMatchProjectionDTO> findBestMatchingCrops(BuyerRequest request);

    public List<CropMatchProjectionDTO> runMatchingQuery(
            BuyerRequest request,
            boolean includePrice,
            boolean includeQuantity,
            boolean includeProducedWay,
            boolean includeQuality,
            boolean includeRegion
    );

}
