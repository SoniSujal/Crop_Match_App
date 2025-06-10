package com.cropMatch.dto.buyerDTO;

import com.cropMatch.model.user.UserDetail;
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

    public BuyerDTO(UserDetail user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.pincode = user.getPincode();
        this.country = user.getCountry();
    }
}