package com.cropMatch.dto.buyerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Integer cropId; // for direct crop buy
    private Integer buyerRequestFarmerId; // for request-based

    private String cropName;
    private String categoryName;
    private String sellerName;
    private String region;

    private String quality;
    private String producedWay;
    private BigDecimal unitPrice;
    private Integer maxQuantity;
    private String unit;
    private String imageUrl;
}
