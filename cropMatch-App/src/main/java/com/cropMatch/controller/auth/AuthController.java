package com.cropMatch.controller.auth;

import com.cropMatch.dto.authDTO.AuthResponseDTO;
import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.authDTO.LoginDTO;
import com.cropMatch.dto.authDTO.RegistrationDTO;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.security.JwtUtil;
import com.cropMatch.service.logout.LogoutService;
import com.cropMatch.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final LogoutService logoutService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody LoginDTO loginDTO) {
        try {
            String username = loginDTO.getUsername();
            String password = loginDTO.getPassword();

            Optional<UserDetail> userOpt = userService.authenticate(username, password);
            if (userOpt.isPresent()) {
                UserDetail user = userOpt.get();
                String role = user.getUserTypes().iterator().next().getUserType().getName();
                String token = jwtUtil.generateToken(user.getEmail(), role);

                AuthResponseDTO dto = new AuthResponseDTO(token, role.toLowerCase(), user.getUsername(), user.getEmail());
                return ResponseEntity.ok(ApiResponse.success(dto));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegistrationDTO user,
                                                        BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append(" "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errors.toString()));
        }

        try {
            userService.register(user);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(HttpServletRequest request,
                                                       HttpServletResponse response) {
        return logoutService.logout(request, response);
    }
}