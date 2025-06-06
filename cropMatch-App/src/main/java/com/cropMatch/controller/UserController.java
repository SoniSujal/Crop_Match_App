package com.cropMatch.controller;

import com.cropMatch.dto.ApiResponse;
import com.cropMatch.dto.UserUpdateDTO;
import com.cropMatch.model.UserDetail;
import com.cropMatch.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDetail>> getUserProfile(Principal principal) {
        try {
//            if (principal == null) {
//                return ResponseEntity.unauthorized().build();
//            }

            String username = principal.getName();
            UserDetail user = userService.findByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch profile: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(@Valid @RequestBody UserUpdateDTO dto,
                                                             BindingResult result,
                                                             Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.unauthorized().build();
//        }

        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append(" "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errors.toString()));
        }

        try {
            String username = principal.getName();
            userService.updateUserProfile(dto, username);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error updating profile: " + e.getMessage()));
        }
    }
}