package com.cropMatch.dto.farmerDTO;

import com.cropMatch.enums.AvailabilityStatus;
import com.cropMatch.enums.ProducedWay;
import com.cropMatch.enums.Quality;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CropDTO {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private int categoryId;

    @NotNull
    private int stockQuantity;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String stockUnit; // e.g., KG, TON

    @NotNull
    private int sellingQuantity;

    @NotBlank
    private String sellingUnit; // e.g., CRATE, BOX

    @NotBlank
    private String region;

    @NotNull
    private YearMonth expireMonth; // Must be a future month

    private String cropType; // Optional type under name like "Heirloom", "Hybrid"

    @NotNull
    private Quality quality; // Enum: LOW, GOOD, BEST

    @NotNull
    private ProducedWay producedWay; // Enum: ORGANIC, CHEMICAL, MIXED

    @NotNull
    private AvailabilityStatus availabilityStatus; // AVAILABLE_NOW or GROWING_READY_IN_FUTURE

    private YearMonth expectedReadyMonth; // Required only if GROWING_READY_IN_FUTURE

    @AssertTrue(message = "expectedReadyMonth must be provided if crop is still growing")
    public boolean isExpectedReadyMonthValid() {
        return availabilityStatus != AvailabilityStatus.GROWING_READY_IN_FUTURE || expectedReadyMonth != null;
    }

    @AssertTrue(message = "expireMonth must be in the future")
    public boolean isExpireMonthInFuture() {
        return expireMonth != null && expireMonth.isAfter(YearMonth.now());
    }
}

