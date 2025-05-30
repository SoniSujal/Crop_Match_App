package com.cropMatch.controller;

import com.cropMatch.dto.ApiResponse;
import com.cropMatch.dto.UserRegistrationDto;
import com.cropMatch.exception.BusinessException;
import com.cropMatch.model.UserDetail;
import com.cropMatch.security.JwtUtil;
import com.cropMatch.service.LogoutService;
import com.cropMatch.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final LogoutService logoutService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto user,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(user);
            return "redirect:/login";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response,
                        Model model) {
        Optional<UserDetail> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            UserDetail user = userOpt.get();
            String role = user.getUserTypes().iterator().next().getUserType().getName();

            String token = jwtUtil.generateToken(user.getEmail(), role);

            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60);
            response.addCookie(jwtCookie);

            return "redirect:/welcome/" + role.toLowerCase();
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/welcome/{role}")
    public String showWelcomePage(@PathVariable String role, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "welcome_" + role.toLowerCase();
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<ApiResponse<Boolean>> logout(HttpServletRequest request,
                                                       HttpServletResponse response) {
        return logoutService.logout(request, response);
    }
}