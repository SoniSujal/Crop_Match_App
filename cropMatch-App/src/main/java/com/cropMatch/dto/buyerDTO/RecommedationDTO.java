package com.cropMatch.dto.buyerDTO;

import com.cropMatch.enums.AvailabilityStatus;
import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import com.cropMatch.model.farmer.CropImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
public class RecommedationDTO {

    @NotBlank
    private String name;

    @NotNull
    private String categoryName;

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

}
