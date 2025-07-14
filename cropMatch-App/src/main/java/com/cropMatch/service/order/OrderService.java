package com.cropMatch.service.order;

import com.cropMatch.dto.buyerDTO.OrderDetailDTO;

public interface OrderService {
   OrderDetailDTO prepareOrderFromCrop(Integer cropId, String username);

    OrderDetailDTO prepareOrderFromRequest(Integer requestId, String username);
}
