package com.cropMatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CropDTO {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String category;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String unit;

    @NotBlank
    private String region;
}
