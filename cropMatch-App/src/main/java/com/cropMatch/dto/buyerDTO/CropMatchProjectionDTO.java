package com.cropMatch.dto.buyerDTO;

import java.math.BigDecimal;

public interface CropMatchProjectionDTO {
    int getId();
    String getName();
    String getRegion();
    BigDecimal getPrice();
    int getSellingQuantity();
    String getSellingUnit();
    String getProducedWay();
    String getQuality();
    int getCreatedBy(); // Farmer ID
    int getMatchScore(); // Mapped from match_score in SQL
}
