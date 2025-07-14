package com.cropMatch.service.BuyerRequest;

import com.cropMatch.enums.ResponseStatus;
import com.cropMatch.dto.buyerDTO.FarmerRequestResponseDTO;
import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.dto.farmerDTO.FarmerDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.buyer.BuyerRequestFarmer;
import com.cropMatch.model.farmer.AvailableCrops;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerRequestFarmerRepository;
import com.cropMatch.repository.buyer.BuyerRequestRepository;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.service.AvailableCrops.AvailableCropsService;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class BuyerRequestServiceImpl implements BuyerRequestService{
    @Autowired
    private BuyerRequestRepository buyerRequestRepository;

    @Autowired
    private AvailableCropsService availableCropsService;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BuyerRequestFarmerRepository buyerRequestFarmerRepository;

    @Override
    public List<BuyerRequest> getAllBuyerRequest() {
        List<BuyerRequest> BuyerRequestAll = buyerRequestRepository.findAll();
        return BuyerRequestAll;
    }

    @Override
    public BuyerRequest saveRequest(BuyerRequest buyerRequest) {
        return buyerRequestRepository.save(buyerRequest);
    }

    @Override
    public AvailableCrops resolveClosestCropName(String inputCropName, Integer categoryId) {
        List<AvailableCrops> crops = availableCropsService.getAllActiveCateRealtedCrops();

        // Filter by category if provided
        if (categoryId != null) {
            crops = crops.stream()
                    .filter(c -> c.getCategory().getId().equals(categoryId))
                    .toList();
        }

        JaroWinklerSimilarity jaro = new JaroWinklerSimilarity();

        for (AvailableCrops crop : crops) {
            double score = jaro.apply(inputCropName.toLowerCase(), crop.getCropName().toLowerCase());
            if (score == 1.0) {
                return crop; // ✅ Early return on perfect match
            }
        }

        return crops.stream()
                .map(c -> {
                    double score = jaro.apply(inputCropName.toLowerCase(), c.getCropName().toLowerCase());
                    return new AbstractMap.SimpleEntry<>(c, score);
                })
                .filter(entry -> entry.getValue() >= 0.83)
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public void handleFarmerResponse(Integer requestId, String farmerUsername, String action) {
        BuyerRequest buyerRequest = buyerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        UserDetail farmerDetail = userService.findByUserEmail(farmerUsername);
        List<BuyerRequestFarmer> byBuyerRequestId = buyerRequestFarmerRepository.findByBuyerRequest_Id(buyerRequest.getId());
        List<BuyerRequestFarmer> buyerRequestFarmers = byBuyerRequestId.stream()
                .filter(buyerRequestFarmer -> Objects.equals(buyerRequestFarmer.getFarmerId(), farmerDetail.getId()))
                .toList();
        BuyerRequestFarmer buyerRequestFarmer = buyerRequestFarmers.get(0);
        buyerRequestFarmer.setBuyerRequest(buyerRequest);
        buyerRequestFarmer.setFarmerId(farmerDetail.getId());
        buyerRequestFarmer.setFarmerStatus(action.equalsIgnoreCase("ACCEPTED") ? ResponseStatus.ACCEPTED : ResponseStatus.REJECTED);
        buyerRequestFarmer.setRespondedOn(LocalDateTime.now());
        buyerRequestFarmerRepository.save(buyerRequestFarmer);
    }

    @Override
    public List<FarmerRequestResponseDTO> getAcceptedOrRejectedRequestsForBuyer(String buyerEmail) {
        UserDetail buyer = userService.findByUserEmail(buyerEmail);
        List<BuyerRequestFarmer> records = buyerRequestFarmerRepository.findByBuyerIdAndAcceptedOrRejected(buyer.getId());

        return records.stream().map(record -> {
            UserDetail farmerUser = userDetailRepository.findById(record.getFarmerId())
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));

            return new FarmerRequestResponseDTO(
                    record.getId(),
                    record.getFarmerStatus().name(),
                    record.getRespondedOn(),
                    new BuyerRequestResponseDTO(record.getBuyerRequest()),
                    new FarmerDTO(farmerUser)
            );
        }).toList();
    }
}
