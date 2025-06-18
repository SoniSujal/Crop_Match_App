package com.cropMatch.dto.buyerDTO;

import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerRequestDTO {

    private String cropName;
    private int required_quantity;
    private String unit;
    private String region;
    private BigDecimal expectedPrice;
    private int categoryId;

    private String quality;
    private String producedWay;
    private LocalDate needByDate;
    private boolean isExpired;

}
