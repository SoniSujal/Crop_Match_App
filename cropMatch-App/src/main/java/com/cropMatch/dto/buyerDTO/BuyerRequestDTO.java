package com.cropMatch.dto.buyerDTO;

import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerRequestDTO {

    private String cropName;
    private String matchedCropName;
    private int requiredQuantity;
    private String unit;
    private String region;
    private BigDecimal expectedPrice;
    private int categoryId;
    private String quality;
    private String producedWay;
    private String needByDate;
}
