package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.dto.buyerDTO.CropMatchProjection;
import com.cropMatch.enums.CropUnit;
import com.cropMatch.enums.ResponseStatus;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.buyer.BuyerRequestFarmer;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerRequestFarmerRepository;
import com.cropMatch.repository.buyer.BuyerRequestRepository;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.repository.crop.CropRepository;
import com.cropMatch.service.user.UserService;
import com.cropMatch.utils.UnitConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuyerMatchingServiceImpl implements BuyerMatchingService{

private final BuyerRequestRepository buyerRequestRepository;
private final BuyerRequestFarmerRepository buyerRequestFarmerRepository;
private final UserService userService;
private final CropRepository cropRepository;
private UnitConverter unitConverter;

    @Override
    public List<CropMatchProjection> findBestMatchingCrops(BuyerRequest request) {
        List<CropMatchProjection> allMatches = runMatchingQuery(
                request, true, true, true, true, true
        );

        List<CropMatchProjection> high = new ArrayList<>();
        List<CropMatchProjection> medium = new ArrayList<>();
        List<CropMatchProjection> low = new ArrayList<>();

        for (CropMatchProjection match : allMatches) {
            int score = match.getMatchScore();
            if (score >= 8) {
                high.add(match);
            } else if (score >= 5) {
                medium.add(match);
            } else {
                low.add(match);
            }
        }

        List<CropMatchProjection> finalList = new ArrayList<>();
        if (high.size() >= 5) {
            finalList.addAll(high);
        } else if (high.size() + medium.size() >= 5) {
            finalList.addAll(high);
            finalList.addAll(medium);
        } else {
            finalList.addAll(high);
            finalList.addAll(medium);
            finalList.addAll(low);
        }
        return finalList;
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
        CropUnit unitConverterBaseUnit = unitConverter.getBaseUnit(request.getUnit());
        double convertBuyerRequiredQuantityInBaseUnit = unitConverter.convert(request.getRequiredQuantity(), request.getUnit(), unitConverterBaseUnit);
        double expectedFinalPrice = ((1000 * Double.parseDouble(String.valueOf(request.getExpectedPrice()))) / convertBuyerRequiredQuantityInBaseUnit);

        return buyerRequestRepository.findMatchingCropsWithFlexibleCriteria(
                cropNameForMatching,
                includeRegion ? request.getRegion() : null,
                includeQuality ? request.getQuality().name() : null,
                includeProducedWay ? request.getProducedWay().name() : null,
                includePrice ? (expectedFinalPrice * 1.15): null,
                includeQuantity ? convertBuyerRequiredQuantityInBaseUnit : null
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
            List<Integer> matchedCropIdsForFarmer = bestMatchingCrops.stream()
                    .filter(crop -> crop.getCreatedBy() == farmerId)
                    .map(CropMatchProjection::getId)
                    .toList();
            match.setCrop(cropRepository.findById(matchedCropIdsForFarmer.get(0)).orElse(null));
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

    @Override
    public List<BuyerRequestResponseDTO> getExpiredRequestsForFarmer(String email){
        UserDetail farmer = userService.findByUserEmail(email);

        List<BuyerRequestFarmer> matches = buyerRequestFarmerRepository.findByFarmerId(farmer.getId());

        List<BuyerRequestResponseDTO> buyerRequestResponseDTOList = new ArrayList<>();
        for (BuyerRequestFarmer match : matches){
            if (match.getFarmerStatus() == ResponseStatus.IS_EXPIRED){
                buyerRequestResponseDTOList.add(new BuyerRequestResponseDTO(match.getBuyerRequest()));
            }
        }
        return buyerRequestResponseDTOList;
    }

    @Override
    public List<BuyerRequestResponseDTO> getSelectedRequestsForFarmer(String email){
        UserDetail farmer = userService.findByUserEmail(email);

        List<BuyerRequestFarmer> matches = buyerRequestFarmerRepository.findByFarmerId(farmer.getId());

        List<BuyerRequestResponseDTO> buyerRequestResponseDTOList = new ArrayList<>();
        for (BuyerRequestFarmer match : matches){
            if (match.getFarmerStatus() == ResponseStatus.SELECTED){
                buyerRequestResponseDTOList.add(new BuyerRequestResponseDTO(match.getBuyerRequest()));
            }
        }
        return buyerRequestResponseDTOList;
    }
}
