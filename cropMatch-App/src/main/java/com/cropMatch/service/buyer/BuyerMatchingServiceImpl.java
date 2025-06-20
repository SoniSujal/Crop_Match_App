package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.dto.buyerDTO.CropMatchProjection;
import com.cropMatch.enums.RequestStatus;
import com.cropMatch.enums.ResponseStatus;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.buyer.BuyerRequestFarmer;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerRequestFarmerRepository;
import com.cropMatch.repository.buyer.BuyerRequestRepository;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.repository.crop.CropRepository;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BuyerMatchingServiceImpl implements BuyerMatchingService{

private final BuyerRequestRepository buyerRequestRepository;
private final BuyerRequestFarmerRepository buyerRequestFarmerRepository;
private final UserService userService;
private final CropRepository cropRepository;

    @Override
    public List<CropMatchProjection> findBestMatchingCrops(BuyerRequest request) {
        List<CropMatchProjection> matches = runMatchingQuery(
                request, true, true, true, true, true
        );

        if (matches.size() >= 5) return matches;

        // 1st fallback: Relax price
        matches = runMatchingQuery(request, false, true, true, true, true);
        if (matches.size() >= 5) return matches;

        // 2nd fallback: Relax quantity
        matches = runMatchingQuery(request, false, false, true, true, true);
        if (matches.size() >= 5) return matches;

        // 3rd fallback: Relax producedWay
        matches = runMatchingQuery(request, false, false, false, true, true);
        if (matches.size() >= 5) return matches;

        // 4th fallback: Relax quality
        matches = runMatchingQuery(request, false, false, false, false, true);
        if (matches.size() >= 5) return matches;

        // 5th fallback: Relax region (only cropName match guaranteed)
        matches = runMatchingQuery(request, false, false, false, false, false);
        return matches;
    }

    @Override
    public List<CropMatchProjection> runMatchingQuery(
            BuyerRequest request,
            boolean includePrice,
            boolean includeQuantity,
            boolean includeProducedWay,
            boolean includeQuality,
            boolean includeRegion
    ) {
        String cropNameForMatching = request.getMatchedCropName() != null ? request.getMatchedCropName() : request.getCropName();
        return buyerRequestRepository.findMatchingCropsWithFlexibleCriteria(
                cropNameForMatching,
                includeRegion ? request.getRegion() : null,
                includeQuality ? request.getQuality().name() : null,
                includeProducedWay ? request.getProducedWay().name() : null,
                includePrice ? request.getExpectedPrice() : null,
                includeQuantity ? request.getRequiredQuantity() : null
        );
    }

    @Override
    public void SendToFarmers(List<CropMatchProjection> bestMatchingCrops, BuyerRequest buyerRequest) {

        List<Integer> farmerIds = bestMatchingCrops.stream().map(farmer -> farmer.getCreatedBy()).toList();
        for (Integer farmerId : farmerIds){
            BuyerRequestFarmer match = new BuyerRequestFarmer();
            match.setBuyerRequest(buyerRequest);
            match.setFarmerId(farmerId);
            match.setFarmerStatus(ResponseStatus.PENDING);
            match.setSentOn(LocalDateTime.now());
            List<Integer> cropMatchingFarmerId = bestMatchingCrops.stream()
                    .filter(farmer12 -> farmer12.getCreatedBy() == farmerId)
                    .map(farmer31 -> farmer31.getId()).toList();
            match.setCrop(cropRepository.findById(cropMatchingFarmerId.get(0)).orElse(null));
            buyerRequestFarmerRepository.save(match);
        }

        if (!farmerIds.isEmpty()){
            buyerRequest.setIsMatched(true);
            buyerRequestRepository.save(buyerRequest);
        }
    }

    @Override
    public List<BuyerRequestResponseDTO> getPendingRequestsForFarmer(String email){
        UserDetail farmer = userService.findByUserEmail(email);

        List<BuyerRequestFarmer> matches = buyerRequestFarmerRepository.findByFarmerId(farmer.getId());

        List<BuyerRequestResponseDTO> buyerRequestResponseDTOList = new ArrayList<>();
        for (BuyerRequestFarmer match : matches){
            if (match.getFarmerStatus() == ResponseStatus.PENDING){
                buyerRequestResponseDTOList.add(new BuyerRequestResponseDTO(match.getBuyerRequest()));
            }
        }
        return buyerRequestResponseDTOList;
    }

    @Override
    public List<BuyerRequestResponseDTO> getAcceptedRequestsForFarmer(String email){
        UserDetail farmer = userService.findByUserEmail(email);

        List<BuyerRequestFarmer> matches = buyerRequestFarmerRepository.findByFarmerId(farmer.getId());

        List<BuyerRequestResponseDTO> buyerRequestResponseDTOList = new ArrayList<>();
        for (BuyerRequestFarmer match : matches){
            if (match.getFarmerStatus() == ResponseStatus.ACCEPTED){
                buyerRequestResponseDTOList.add(new BuyerRequestResponseDTO(match.getBuyerRequest()));
            }
        }
        return buyerRequestResponseDTOList;
    }

    @Override
    public List<BuyerRequestResponseDTO> getRejectedRequestsForFarmer(String email){
        UserDetail farmer = userService.findByUserEmail(email);

        List<BuyerRequestFarmer> matches = buyerRequestFarmerRepository.findByFarmerId(farmer.getId());

        List<BuyerRequestResponseDTO> buyerRequestResponseDTOList = new ArrayList<>();
        for (BuyerRequestFarmer match : matches){
            if (match.getFarmerStatus() == ResponseStatus.CLOSED){
                buyerRequestResponseDTOList.add(new BuyerRequestResponseDTO(match.getBuyerRequest()));
            }
        }
        return buyerRequestResponseDTOList;
    }
}
