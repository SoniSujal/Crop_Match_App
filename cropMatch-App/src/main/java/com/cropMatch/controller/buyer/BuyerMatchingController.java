package com.cropMatch.controller.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.model.common.UserPrincipal;
import com.cropMatch.service.buyer.BuyerMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/farmer/buyer-requests")
@RequiredArgsConstructor
public class BuyerMatchingController {

    private final BuyerMatchingService matchingService;

    @GetMapping("/pending")
    public ResponseEntity<List<BuyerRequestResponseDTO>> getBuyerRequestsPending(@AuthenticationPrincipal UserPrincipal userDetails) {
        List<BuyerRequestResponseDTO> requests = matchingService.getPendingRequestsForFarmer(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/accepted")
    public ResponseEntity<List<BuyerRequestResponseDTO>> getBuyerRequestsAccepted(@AuthenticationPrincipal UserPrincipal userDetails) {
        List<BuyerRequestResponseDTO> requests = matchingService.getAcceptedRequestsForFarmer(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<BuyerRequestResponseDTO>> getBuyerRequestsRejected(@AuthenticationPrincipal UserPrincipal userDetails) {
        List<BuyerRequestResponseDTO> requests = matchingService.getRejectedRequestsForFarmer(userDetails.getUsername())    ;
        return ResponseEntity.ok(requests);
    }
}