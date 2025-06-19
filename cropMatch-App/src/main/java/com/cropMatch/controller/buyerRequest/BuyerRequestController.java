package com.cropMatch.controller.buyerRequest;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;

import com.cropMatch.dto.buyerDTO.FarmerRequestResponseDTO;
import com.cropMatch.dto.buyerDTO.CropMatchProjection;
import com.cropMatch.dto.buyerDTO.CropSuggestionDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.common.UserPrincipal;
import com.cropMatch.model.farmer.AvailableCrops;
import com.cropMatch.service.AvailableCrops.AvailableCropsService;
import com.cropMatch.service.BuyerRequest.BuyerRequestService;
import com.cropMatch.service.buyer.BuyerMatchingService;
import com.cropMatch.service.buyer.BuyerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/buyer/buyerRequest")
@CrossOrigin(origins = "*")
public class BuyerRequestController {

    @Autowired
    private final AvailableCropsService availableCropsService;

    private final BuyerService buyerService;

    private final BuyerMatchingService buyerMatchingService;

    private final BuyerRequestService buyerRequestService;

    @GetMapping("/initial-crops")
    public ResponseEntity<List<CropSuggestionDTO>> getInitialCrops(
            @RequestParam(required = false) Integer categoryId) {

        List<AvailableCrops> crops = availableCropsService.getAllActiveCateRealtedCrops();

        if (categoryId != null) {
            crops = crops.stream()
                    .filter(c -> c.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        List<CropSuggestionDTO> result = crops.stream()
                .map(c -> new CropSuggestionDTO(c.getCropName(), c.getCategory().getId(), 1.0))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/suggest-crops")
    public ResponseEntity<List<CropSuggestionDTO>> suggestCrops(
            @RequestParam String query,
            @RequestParam(required = false) Integer categoryId) {

        List<AvailableCrops> crops = availableCropsService.getAllActiveCateRealtedCrops();

        if (categoryId != null) {
            crops = crops.stream()
                    .filter(c -> c.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        JaroWinklerSimilarity jaro = new JaroWinklerSimilarity();

        List<CropSuggestionDTO> suggestions = crops.stream()
                .map(c -> {
                    double score = jaro.apply(query.toLowerCase(), c.getCropName().toLowerCase());
                    return new CropSuggestionDTO(c.getCropName(), c.getCategory().getId(), score);
                })
                .filter(dto -> dto.getScore() >= 0.7)
                .sorted(Comparator.comparingDouble(CropSuggestionDTO::getScore).reversed())
                .limit(7)
                .collect(Collectors.toList());

        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/all-crops-name")
    public ResponseEntity<List<AvailableCrops>> getAllCropsName() {
        List<AvailableCrops> allActiveCateRealtedCrops = availableCropsService.getAllActiveCateRealtedCrops();
        return ResponseEntity.ok(allActiveCateRealtedCrops);
    }

    @GetMapping("/crop-category-mapping")
    public ResponseEntity<Map<String, Integer>> getCropCategoryMapping() {
        List<AvailableCrops> allCrops = availableCropsService.getAllActiveCateRealtedCrops();
        Map<String, Integer> mapping = allCrops.stream()
                .collect(Collectors.toMap(AvailableCrops::getCropName, c -> c.getCategory().getId()));
        return ResponseEntity.ok(mapping);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@RequestBody BuyerRequestDTO buyerRequestDTO, Principal principal){
        BuyerRequest buyerRequest = buyerService.createRequest(buyerRequestDTO, principal.getName());
        List<CropMatchProjection> bestMatchingCrops = buyerMatchingService.findBestMatchingCrops(buyerRequest);
        buyerMatchingService.SendToFarmers(bestMatchingCrops, buyerRequest);
        return ResponseEntity.ok(buyerRequest);
    }

    @GetMapping
    public ResponseEntity<?> getAllRequests(Principal principal) {
        return ResponseEntity.ok(buyerService.getAllRequests(principal.getName()));
    }

    @GetMapping("/status")
    public ResponseEntity<List<FarmerRequestResponseDTO>> getAcceptedOrRejectedResponses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<FarmerRequestResponseDTO> responses = buyerRequestService.getAcceptedOrRejectedRequestsForBuyer(userPrincipal.getUsername());
        return ResponseEntity.ok(responses);
    }


}
