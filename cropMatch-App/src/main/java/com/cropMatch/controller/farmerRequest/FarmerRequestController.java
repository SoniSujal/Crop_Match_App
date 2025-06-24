package com.cropMatch.controller.farmerRequest;

import com.cropMatch.model.common.UserPrincipal;
import com.cropMatch.service.BuyerRequest.BuyerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/farmer")
public class FarmerRequestController {

    @Autowired
    private BuyerRequestService buyerRequestService;

    @PostMapping("/respond/{requestId}")
    public ResponseEntity<String> respondToRequest(
            @PathVariable Integer requestId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal userDetails
    ) {
        String action = body.get("action"); // "ACCEPTED" or "REJECTED"
        String farmerUsername = userDetails.getUsername();

        buyerRequestService.handleFarmerResponse(requestId, farmerUsername, action);
        return ResponseEntity.ok("Response recorded");
    }

}
