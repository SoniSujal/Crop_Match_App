package com.cropMatch.model.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.enums.CropUnit;
import com.cropMatch.enums.RequestStatus;
import com.cropMatch.model.admin.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private int quantity;

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

    public BuyerRequest(BuyerRequestDTO buyerRequestDTO, Category category, Integer buyerId) {
        this.cropName = buyerRequestDTO.getCropName();
        this.category = category;
        this.quantity = buyerRequestDTO.getQuantity();
        this.unit = CropUnit.valueOf(buyerRequestDTO.getUnit());
        this.region = buyerRequestDTO.getRegion();
        this.expectedPrice = buyerRequestDTO.getExpectedPrice();
        this.buyerId = buyerId;
        this.createdOn = LocalDateTime.now();
    }
}
