package com.cropMatch.controller.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.service.buyer.BuyerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("api/buyer")
public class BuyerController {

    private final BuyerService buyerService;

    @GetMapping
    public String showBuyersPage() {
        return "buyers";
    }

    @GetMapping("/requests/new")
    public ResponseEntity<?> ShowRequestPage(){
        return ResponseEntity.ok(buyerService.getAllRequests());
    }

    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(@RequestBody BuyerRequestDTO dto, Principal principal){
        BuyerRequest saved = buyerService.createRequest(dto, principal.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getActiveCategories(){
        return ResponseEntity.ok(buyerService.getActiveCategories());
    }

    @GetMapping("/units")
    public ResponseEntity<?> getAllUnits() {
        return ResponseEntity.ok(buyerService.getAllUnits());
    }


}
