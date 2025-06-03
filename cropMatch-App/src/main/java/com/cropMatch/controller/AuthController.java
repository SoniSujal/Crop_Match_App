package com.cropMatch.controller;

import com.cropMatch.dto.ApiResponse;
import com.cropMatch.model.UserDetail;
import com.cropMatch.security.JwtUtil;
import com.cropMatch.service.logout.LogoutService;
import com.cropMatch.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class AuthController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final LogoutService logoutService;

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

            return "redirect:/" + role.toLowerCase();
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/{role}")
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
