package com.cropMatch.controller;

import com.cropMatch.dto.BuyerDTO;
import com.cropMatch.dto.FarmerDTO;
import com.cropMatch.model.UserDetail;
import com.cropMatch.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
//@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

//    @GetMapping("/List-Farmers")
//    public List<UserDetail> getAllFarmers() {
//        List<UserDetail> farmers = adminService.getAllFarmers("FARMER");
//        System.out.println("Fetched Farmers: " + farmers.size());
//        farmers.forEach(f -> System.out.println(f.getEmail()));
//        return farmers;
//    }

    @GetMapping("/List-Farmers")
    public List<FarmerDTO> getAllFarmers() {
        return adminService.getAllFarmers("FARMER").stream()
                .map(user -> new FarmerDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getMobile()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/List-Buyers")
    public List<BuyerDTO> getAllBuyers() {
        return adminService.getAllBuyers("BUYER").stream()
                .map(user -> new BuyerDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getMobile()
                ))
                .collect(Collectors.toList());
    }
}
