package com.cropMatch.dto.farmerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FarmerDTO {
    private Integer id;
    private String username;
    private String email;
    private String mobile;
    private String pincode;
    private String country;
}