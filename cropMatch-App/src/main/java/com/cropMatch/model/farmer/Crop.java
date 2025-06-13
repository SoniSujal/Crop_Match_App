package com.cropMatch.model.farmer;

import com.cropMatch.converter.YearMonthAttributeConverter;
import com.cropMatch.dto.farmerDTO.CropDTO;
import com.cropMatch.enums.AvailabilityStatus;
import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import com.cropMatch.model.admin.Category;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crop_id")
    private int id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "stock_unit", nullable = false)
    private String stockUnit;

    @Column(name = "selling_unit", nullable = false)
    private String sellingUnit;

    @Column(nullable = false)
    private int createdBy;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private String region;

    @OneToMany(mappedBy = "crop", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CropImage> images;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "expire_month", nullable = false)
//    @Convert(converter = YearMonthAttributeConverter.class)
    private String expireMonth;

    @Column(name = "crop_type")
    private String cropType;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality", nullable = false)
    private Quality quality;

    @Enumerated(EnumType.STRING)
    @Column(name = "produced_way", nullable = false)
    private ProducedWay producedWay;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Column(name = "expected_ready_month")
//    @Convert(converter = YearMonthAttributeConverter.class)
    private String expectedReadyMonth;

    public Crop(CropDTO cropDTO,Category category,Integer farmerId) {
        this.name = cropDTO.getName();
        this.description = cropDTO.getDescription();
        this.category = category;
        this.quantity = cropDTO.getQuantity();
        this.price = cropDTO.getPrice();
        this.stockUnit = cropDTO.getStockUnit();
        this.sellingUnit = cropDTO.getSellingUnit();
        this.createdBy = farmerId;
        this.status = true;
        this.region = cropDTO.getRegion();
        this.createdOn = LocalDateTime.now();
        this.updatedOn = LocalDateTime.now();
        this.expireMonth = String.valueOf(cropDTO.getExpireMonth());
        this.cropType = cropDTO.getCropType();
        this.quality = cropDTO.getQuality();
        this.producedWay = cropDTO.getProducedWay();
        this.availabilityStatus = cropDTO.getAvailabilityStatus();
        this.expectedReadyMonth = String.valueOf(cropDTO.getExpectedReadyMonth());
    }

}
