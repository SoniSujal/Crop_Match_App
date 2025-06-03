package com.cropMatch.controller;

import com.cropMatch.dto.ApiResponse;
import com.cropMatch.dto.UserRegistrationDto;
import com.cropMatch.dto.UserUpdateDto;
import com.cropMatch.exception.BusinessException;
import com.cropMatch.model.UserDetail;
import com.cropMatch.security.JwtUtil;
import com.cropMatch.service.AdminService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    private final LogoutService logoutService;

    @Autowired
    private AdminService adminService;

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

    // ✅ Added for routing to static farmers and buyers pages
    @GetMapping("/farmers")
    public String showFarmersPage() {
        return "farmers";
    }

    @GetMapping("/buyers")
    public String showBuyersPage() {
        return "buyers";
    }


    @GetMapping("/updateProfile")
    public String showEditProfileForm(Model model,Principal principal){

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();  // gives us the username
        UserDetail userDetail = userService.findByUsername(username);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername(userDetail.getUsername());
        dto.setEmail(userDetail.getEmail());
        dto.setMobile(userDetail.getMobile());
        dto.setPincode(userDetail.getPincode());
        dto.setCountry(userDetail.getCountry());

        model.addAttribute("user",dto);

        return "edit_profile";
    }


    @PostMapping("/updateProfile")
    public String processUpdateProfile( @ModelAttribute("user") UserUpdateDto dto,BindingResult result,Model model,Principal principal,RedirectAttributes redirectAttributes)
    {
        if (principal == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "edit_profile";
        }

        String username = principal.getName();
        String role = "farmer"; // fallback--default

        try {
            UserDetail user = userService.findByUsername(username);
            role = user.getUserTypes()
                    .stream()
                    .findFirst()
                    .map(mapping -> mapping.getUserType().getName().toLowerCase())
                    .orElse("farmer");

            userService.updateUserProfile(dto, username);
            redirectAttributes.addFlashAttribute("updateSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("updateSuccess", false);
        }
        return "redirect:/welcome/" + role;
    }

    @GetMapping("admin/edit/{username}")
    public String showEditForm(@PathVariable("username") String username, Model model) {
        UserDetail user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "redirect:/";  // Redirect to the home page if user not found
        }
        model.addAttribute("user", user);
        return "edit_user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") UserUpdateDto user1,BindingResult result,Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            String email = user1.getUsername();
            UserDetail user = userService.findByUserEmail(email);
            userService.updateUserProfile(user1, user.getUsername());
            model.addAttribute("message", "User updated successfully!");

            redirectAttributes.addFlashAttribute("updateSuccess", true);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user. Please try again.");
            redirectAttributes.addFlashAttribute("updateSuccess", false);
        }
        return "redirect:/admin";
    }

    @GetMapping("admin/delete/{username}")
    public String deleteUser(@PathVariable("username") String username, Model model,RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(username) == null) {
                model.addAttribute("errorMessage", "User not found.");
                return "redirect:/admin";  // Redirect to home page if user is not found
            }
            adminService.deleteBYUserName(username);
            model.addAttribute("message", "User deleted successfully!");
            redirectAttributes.addFlashAttribute("DeleteSuccess", true);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting user. Please try again.");
            redirectAttributes.addFlashAttribute("DeleteSuccess", false);
        }
        return "redirect:/admin";
    }
}