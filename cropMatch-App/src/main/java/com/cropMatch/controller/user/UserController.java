package com.cropMatch.controller.user;

import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.common.UserUpdateDTO;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.service.buyer.BuyerService;
import com.cropMatch.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    private final BuyerService buyerService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserProfile(Principal principal) {
        try {
            if (principal == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            String username = principal.getName();
            UserDetail user = userService.findByUserEmail(username);

            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("mobile", user.getMobile());
            data.put("pincode", user.getPincode());
            data.put("country", user.getCountry());
            data.put("region",user.getRegion());

            String role = user.getUserTypes().stream()
                    .findFirst()
                    .map(mapping -> mapping.getUserType().getName())
                    .orElse("UNKNOWN");
            data.put("role", role);

            if ("BUYER".equalsIgnoreCase(role)) {
                data.put("preferenceCategoryIds", buyerService.getBuyerPreferenceCategoryIds(user.getId()));
            }

            return ResponseEntity.ok(ApiResponse.success(data));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch profile: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(@Valid @RequestBody UserUpdateDTO dto,
                                                             BindingResult result,
                                                             Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append(" "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errors.toString()));
        }

        try {
            String username = principal.getName();
            userService.updateUserProfile(dto, username);

            if (dto.getPreferenceCategoryIds() != null && !dto.getPreferenceCategoryIds().isEmpty()) {
                buyerService.updateBuyerPreferences(username, dto.getPreferenceCategoryIds());
            }

            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error updating profile: " + e.getMessage()));
        }
    }
}