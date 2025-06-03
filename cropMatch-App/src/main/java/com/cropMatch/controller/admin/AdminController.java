package com.cropMatch.controller.admin;

import com.cropMatch.dto.BuyerDTO;
import com.cropMatch.dto.FarmerDTO;
import com.cropMatch.dto.UserUpdateDTO;
import com.cropMatch.model.UserDetail;
import com.cropMatch.service.admin.AdminService;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

//@RestController
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private final AdminService adminService;

    @Autowired
    private final UserService userService;

    @GetMapping("/List-Farmers")
    @ResponseBody
    public List<FarmerDTO> getAllFarmers() {
        return adminService.getAllUsersByRole("FARMER").stream()
                .map(user -> new FarmerDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getMobile(),
                        user.getPincode(),
                        user.getCountry()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/List-Buyers")
    @ResponseBody
    public List<BuyerDTO> getAllBuyers() {
        return adminService.getAllUsersByRole("BUYER").stream()
                .map(user -> new BuyerDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getMobile(),
                        user.getCountry(),
                        user.getPincode()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/edit/{username}")
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
    public String updateUser(
            @ModelAttribute("user") UserUpdateDTO userDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String email = userDto.getEmail();
            UserDetail user = userService.findByUserEmail(email);

            if (user == null) {
                model.addAttribute("errorMessage", "User not found.");
                redirectAttributes.addFlashAttribute("updateSuccess", false);
                return "redirect:/admin";
            }

            userService.updateUserProfile(userDto, user.getUsername());

            redirectAttributes.addFlashAttribute("updateSuccess", true);
            return "redirect:/admin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user. Please try again.");
            redirectAttributes.addFlashAttribute("updateSuccess", false);
            return "redirect:/admin";
        }
    }

    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable("username") String username, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(username) == null) {
                model.addAttribute("errorMessage", "User not found.");
                return "redirect:/admin";  // Redirect to home page if user is not found
            }
            int deletUserByName = userService.deletUserByName(username);
            if(deletUserByName == 0) {
                model.addAttribute("errorMessage", "Not User Found. Please try again.");
                redirectAttributes.addFlashAttribute("DeleteSuccess", false);
            }
            model.addAttribute("message", "User deleted successfully!");
            redirectAttributes.addFlashAttribute("DeleteSuccess", true);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting user. Please try again.");
            redirectAttributes.addFlashAttribute("DeleteSuccess", false);
        }
        return "redirect:/admin";
    }
}
