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

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false  )
    private Category category;

    @Column(nullable = false)
    private int required_quantity;

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

    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Quality quality;

    @Enumerated(EnumType.STRING)
    @Column(name = "produced_way", nullable = false)
    private ProducedWay producedWay;

    @Column(name = "need_by_date")
    private LocalDate needByDate;

    @Column(name = "is_expired")
    private boolean isExpired;

    @Column(name = "is_matched", nullable = false)
    private boolean isMatched = false;

    public BuyerRequest(BuyerRequestDTO buyerRequestDTO, Category category, Integer buyerId) {
        this.cropName = buyerRequestDTO.getCropName();
        this.category = category;
        this.required_quantity = buyerRequestDTO.getRequired_quantity();
        this.unit = CropUnit.valueOf(buyerRequestDTO.getUnit());
        this.region = buyerRequestDTO.getRegion();
        this.expectedPrice = buyerRequestDTO.getExpectedPrice();
        this.buyerId = buyerId;
        this.createdOn = LocalDateTime.now();

        this.quality = Quality.valueOf(buyerRequestDTO.getQuality().toUpperCase());
        this.producedWay = ProducedWay.valueOf(buyerRequestDTO.getProducedWay().toUpperCase());
        this.needByDate = buyerRequestDTO.getNeedByDate();
        this.isExpired = buyerRequestDTO.isExpired();
    }
}
