package com.cropMatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerDTO {
    private Integer id;
    private String username;
    private String email;
    private String mobile;
    private String pincode;
    private String country;
}