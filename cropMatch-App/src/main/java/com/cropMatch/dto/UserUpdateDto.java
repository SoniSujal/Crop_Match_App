package com.cropMatch.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @NotBlank(message = "Username is Required!")
    private String username;

    @Pattern(regexp = "\\d{10}",message = "Mobile number must be exactly 10 digits")
    private String mobile;

    @Pattern(regexp = "pincode must be of 6 digits only!")
    private String pincode;

    @Pattern(regexp = "^[A-Za-z]{1,30}$",message = "Country must be alphabetic and max 30 characters")
    private String country;

}
