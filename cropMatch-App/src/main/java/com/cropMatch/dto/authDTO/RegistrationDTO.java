package com.cropMatch.dto.authDTO;

import com.cropMatch.enums.UserRoles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String mobile;

    @NotBlank
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoles userType;

    @NotBlank
    private String pincode;

    @NotBlank
    private String country;

    private List<Integer> preferenceCategoryIds;

}
