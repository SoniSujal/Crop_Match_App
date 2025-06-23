package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.dto.buyerDTO.CropMatchProjection;
import com.cropMatch.model.buyer.BuyerRequest;

import java.util.List;

public interface BuyerMatchingService {

    void SendToFarmers(List<CropMatchProjection> bestMatchingCrops, BuyerRequest request);

    List<BuyerRequestResponseDTO> getPendingRequestsForFarmer(String email);

    public List<BuyerRequestResponseDTO> getRejectedRequestsForFarmer(String email);

    List<BuyerRequestResponseDTO> getExpiredRequestsForFarmer(String email);

    List<BuyerRequestResponseDTO> getSelectedRequestsForFarmer(String email);

    public List<BuyerRequestResponseDTO> getAcceptedRequestsForFarmer(String email);

    public List<CropMatchProjection> findBestMatchingCrops(BuyerRequest request);

    public List<CropMatchProjection> runMatchingQuery(
            BuyerRequest request,
            boolean includePrice,
            boolean includeQuantity,
            boolean includeProducedWay,
            boolean includeQuality,
            boolean includeRegion
    );

}
