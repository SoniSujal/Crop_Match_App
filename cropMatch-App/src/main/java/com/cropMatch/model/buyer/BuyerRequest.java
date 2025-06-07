package com.cropMatch.model.buyer;

import com.cropMatch.enums.CropUnit;
import com.cropMatch.enums.RequestStatus;
import com.cropMatch.model.admin.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
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
}
