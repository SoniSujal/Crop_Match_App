package com.cropMatch.controller;

import com.cropMatch.dto.UserUpdateDTO;
import com.cropMatch.model.UserDetail;
import com.cropMatch.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/updateProfile")
    public String showEditProfileForm(Model model, Principal principal){

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();  // gives us the username
        UserDetail userDetail = userService.findByUsername(username);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername(userDetail.getUsername());
        dto.setEmail(userDetail.getEmail());
        dto.setMobile(userDetail.getMobile());
        dto.setPincode(userDetail.getPincode());
        dto.setCountry(userDetail.getCountry());

        model.addAttribute("user",dto);

        return "edit_profile";
    }


    @PostMapping("/updateProfile")
    public String processUpdateProfile(@ModelAttribute("user") UserUpdateDTO dto, BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttributes)
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

//    @PostMapping("/update")
//    public String updateUser(@ModelAttribute("user") UserUpdateDTO user1, BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttributes) {
//        if (principal == null) {
//            return "redirect:/login";
//        }
//        try {
//            String email = user1.getEmail();
//            UserDetail user = userService.findByUserEmail(email);
//            userService.updateUserProfile(user1, user.getUsername());
//            model.addAttribute("message", "User updated successfully!");
//
//            redirectAttributes.addFlashAttribute("updateSuccess", true);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "Error updating user. Please try again.");
//            redirectAttributes.addFlashAttribute("updateSuccess", false);
//        }
//        return "redirect:/admin";
//    }
}
