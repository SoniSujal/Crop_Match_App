package com.cropMatch.model.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.enums.CropUnit;
import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import com.cropMatch.enums.RequestStatus;
import com.cropMatch.model.admin.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String cropName;

    @Column(name = "matched_crop_name")
    private String matchedCropName;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false  )
    private Category category;

    @Column(nullable = false)
    private int requiredQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropUnit unit;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private BigDecimal expectedPrice;

    @Column(nullable = false)
    private int buyerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Quality quality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProducedWay producedWay;

    @Column
    private LocalDate needByDate;

    @Column(nullable = false)
    private Boolean isExpired;

    @Column(nullable = false)
    private Boolean isMatched = true;

    @Column(name = "created_on",insertable = false,updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on",updatable = false,insertable = false)
    private LocalDateTime updatedOn;

    public BuyerRequest(BuyerRequestDTO buyerRequestDTO, Category category, Integer buyerId) {
        this.cropName = buyerRequestDTO.getCropName();
        this.matchedCropName = buyerRequestDTO.getMatchedCropName();
        this.category = category;
        this.requiredQuantity = buyerRequestDTO.getRequiredQuantity();
        this.unit = CropUnit.valueOf(buyerRequestDTO.getUnit().toUpperCase());
        this.region = buyerRequestDTO.getRegion();
        this.expectedPrice = buyerRequestDTO.getExpectedPrice();
        this.buyerId = buyerId;
        this.quality = Quality.valueOf(buyerRequestDTO.getQuality().toUpperCase());
        this.producedWay = ProducedWay.valueOf(buyerRequestDTO.getProducedWay().toUpperCase());
        this.needByDate = LocalDate.parse(buyerRequestDTO.getNeedByDate());
        this.isExpired = false;
    }
}
