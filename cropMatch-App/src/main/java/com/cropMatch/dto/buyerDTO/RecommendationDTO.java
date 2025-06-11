package com.cropMatch.dto.buyerDTO;

import com.cropMatch.enums.AvailabilityStatus;
import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import com.cropMatch.model.farmer.Crop;
import com.cropMatch.model.farmer.CropImage;
import com.cropMatch.service.user.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {

    @Autowired
    private UserService userService;

    @NotBlank
    private String name;

    @NotNull
    private String categoryName;

    @NotNull
    private String sellerName;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String stockUnit;

    @NotBlank
    private String sellingUnit;

    @NotBlank
    private String region;

    @NotNull
    private String expireMonth;

    private String cropType;

    @NotNull
    private Quality quality;

    @NotNull
    private ProducedWay producedWay;

    @NotNull
    private AvailabilityStatus availabilityStatus;

    private String expectedReadyMonth;

    private List<CropImage> images;

    public RecommendationDTO(Crop cropDetails) {
        this.name = cropDetails.getName();
        this.categoryName = cropDetails.getCategory().getName();
        this.sellerName = userService.findByUsernameUsingId(cropDetails.getCreatedBy());
        this.quantity = cropDetails.getQuantity();
        this.price = cropDetails.getPrice();
        this.stockUnit = cropDetails.getStockUnit();
        this.sellingUnit = cropDetails.getSellingUnit();
        this.region = cropDetails.getRegion();
        this.expireMonth = cropDetails.getExpireMonth();
        this.cropType = cropDetails.getCropType();
        this.quality = cropDetails.getQuality();
        this.producedWay = cropDetails.getProducedWay();
        this.availabilityStatus = cropDetails.getAvailabilityStatus();
        this.expectedReadyMonth = cropDetails.getExpectedReadyMonth();
        this.images = cropDetails.getImages();
    }
}
