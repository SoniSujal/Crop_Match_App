package com.cropMatch.controller.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.service.buyer.BuyerMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/buyer-requests")
@RequiredArgsConstructor
public class BuyerMatchingController {

    private final BuyerMatchingService matchingService;

    @GetMapping
    public ResponseEntity<List<BuyerRequestResponseDTO>> getFarmerRequests(Principal principal) {
        List<BuyerRequestResponseDTO> requests = matchingService.getPendingRequestsForFarmer(principal.getName());
        return ResponseEntity.ok(requests);
    }


}