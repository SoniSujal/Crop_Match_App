package com.cropMatch.controller.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.service.buyer.BuyerService;
import com.cropMatch.service.crop.CropService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("api/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {

    private final BuyerService buyerService;

    private final CropService cropService;

    @GetMapping
    public String showBuyersPage() {
        return "buyers";
    }

    @PostMapping("/requests/create")
    public ResponseEntity<?> createRequest(@RequestBody BuyerRequestDTO dto, Principal principal){
        BuyerRequest saved = buyerService.createRequest(dto, principal.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getAllRequests(Principal principal) {
        return ResponseEntity.ok(buyerService.getAllRequests(principal.getName()));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getActiveCategories(){
        return ResponseEntity.ok(buyerService.getActiveCategories());
    }

    @GetMapping("/units")
    public ResponseEntity<?> getAllUnits() {
        return ResponseEntity.ok(buyerService.getAllUnits());
    }

    @PostMapping("/recommendations")
    public ResponseEntity<List<RecommendationDTO>> getRecommendationsByCategories(@RequestBody List<Integer> categoryIds) {
        return ResponseEntity.ok(cropService.recommedCropsDetailsBaseCategory(categoryIds));
    }
}
