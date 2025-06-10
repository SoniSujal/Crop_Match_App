package com.cropMatch.model.farmer;

import com.cropMatch.enums.AvailabilityStatus;
import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import com.cropMatch.model.admin.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Entity
@Data
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
    private List<CropImage> images;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "expire_month", nullable = false)
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
    private String expectedReadyMonth;
}
