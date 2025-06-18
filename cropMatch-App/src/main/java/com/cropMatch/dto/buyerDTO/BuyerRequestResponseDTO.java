package com.cropMatch.dto.buyerDTO;

import com.cropMatch.model.buyer.BuyerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerRequestResponseDTO {
    private int id;
    private String cropName;
    private int required_quantity;
    private String unit;
    private String region;
    private BigDecimal expectedPrice;
    private String status;
    private String categoryName;
    private LocalDateTime createdOn;

    private String quality;
    private String producedWay;
    private LocalDate needByDate;
    private boolean isExpired;
    private boolean isMatched;

    public BuyerRequestResponseDTO(BuyerRequest request) {
        this.id = request.getId();
        this.cropName = request.getCropName();
        this.required_quantity = request.getRequired_quantity();
        this.unit = request.getUnit().name();
        this.region = request.getRegion();
        this.expectedPrice = request.getExpectedPrice();
        this.status = request.getStatus().name();
        this.categoryName = request.getCategory().getName();
        this.createdOn = request.getCreatedOn();

        this.quality = request.getQuality().name();
        this.producedWay = request.getProducedWay().name();
        this.needByDate = request.getNeedByDate();
        this.isExpired = request.isExpired();
        this.isMatched = request.isMatched();
    }
}
