package com.cropMatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerRequestDTO {

    private String cropName;
    private int quantity;
    private String unit;
    private String region;
    private BigDecimal expectedPrice;
    private int categoryId;

}
