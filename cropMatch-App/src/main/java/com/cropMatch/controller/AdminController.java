package com.cropMatch.controller;

import com.cropMatch.dto.BuyerDTO;
import com.cropMatch.dto.FarmerDTO;
import com.cropMatch.service.AdminService;
import com.cropMatch.service.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
//@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
