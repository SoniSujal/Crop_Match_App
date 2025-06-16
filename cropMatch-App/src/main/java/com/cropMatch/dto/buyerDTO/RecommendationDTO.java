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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecommendationDTO {

    @NotBlank
    private Integer cropId;

    @NotBlank
    private String name;

    private String Desc;

    @NotNull
    private String categoryName;

    @NotNull
    private String sellerName;

    @NotNull
    private int stockQuantity;

    @NotNull
    private int sellingQuantity;

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

    private List<String> imagePaths;

    public RecommendationDTO(Crop cropDetails,UserService userService) {
        this.cropId = cropDetails.getId();
        this.name = cropDetails.getName();
        this.Desc = cropDetails.getDescription();
        this.categoryName = cropDetails.getCategory().getName();
        this.sellerName = userService.findByUsernameUsingId(cropDetails.getCreatedBy());
        this.stockQuantity = cropDetails.getStockQuantity();
        this.sellingQuantity = cropDetails.getSellingQuantity();
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
        this.imagePaths = cropDetails.getImages().stream()
                .map(CropImage::getImagePath)
                .collect(Collectors.toList());
    }
}
