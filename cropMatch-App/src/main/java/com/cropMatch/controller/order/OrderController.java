package com.cropMatch.controller.order;

import com.cropMatch.dto.buyerDTO.OrderDetailDTO;
import com.cropMatch.model.common.UserPrincipal;
import com.cropMatch.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("api/buyer/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/create")
    public ResponseEntity<OrderDetailDTO> getOrderDetails(@RequestParam(required = false) Integer cropId,
                                                          @RequestParam(required = false) Integer requestId,
                                                          @AuthenticationPrincipal UserPrincipal buyer){
        if (cropId!=null){
            return ResponseEntity.ok(orderService.prepareOrderFromCrop(cropId,buyer.getUsername()));
        } else if (requestId != null) {
            return ResponseEntity.ok(orderService.prepareOrderFromRequest(requestId,buyer.getUsername()));
        }else {
            throw new IllegalArgumentException("Either cropId or requestId must be provided");
        }
    }


}








