package com.cropMatch.dto.buyerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CropSuggestionDTO {

    private String name;
    private Integer categoryId;
    private double score;
}
