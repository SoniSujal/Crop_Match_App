package com.cropMatch.service.BuyerRequest;

import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.farmer.AvailableCrops;
import com.cropMatch.repository.buyer.BuyerRequestRepository;
import com.cropMatch.service.AvailableCrops.AvailableCropsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerRequestServiceImpl implements BuyerRequestService{

    private BuyerRequestRepository buyerRequestRepository;

    @Autowired
    private AvailableCropsService availableCropsService;

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
}
