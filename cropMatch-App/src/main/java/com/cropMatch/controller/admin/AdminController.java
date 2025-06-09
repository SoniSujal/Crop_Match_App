package com.cropMatch.controller.admin;

import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.buyerDTO.BuyerDTO;
import com.cropMatch.dto.farmerDTO.FarmerDTO;
import com.cropMatch.dto.common.UserUpdateDTO;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.service.admin.AdminService;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    private final UserService userService;

    @GetMapping("/farmers")
    public ResponseEntity<ApiResponse<List<FarmerDTO>>> getAllActiveFarmers() {
        try {
            List<FarmerDTO> farmers = adminService.getAllUsersByRole("FARMER").stream().filter(UserDetail::getActive)
                    .map( FarmerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(farmers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch farmers: " + e.getMessage()));
        }
    }

    @GetMapping("/farmers/all")
    public ResponseEntity<ApiResponse<List<FarmerDTO>>> getAllFarmersIncludingDeleted() {
        try {
            List<FarmerDTO> farmers = adminService.getAllUsersByRole("FARMER")
                    .stream().map(FarmerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(farmers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch all farmers: " + e.getMessage()));
        }
    }

    @GetMapping("/farmers/deleted")
    public ResponseEntity<ApiResponse<List<FarmerDTO>>> getDeletedFarmers() {
        try {
            List<FarmerDTO> deletedFarmers = adminService.getAllUsersByRole("FARMER")
                    .stream().filter(user -> !user.getActive())
                    .map(FarmerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(deletedFarmers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch deleted farmers: " + e.getMessage()));
        }
    }

    @GetMapping("/buyers")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getAllActiveBuyers() {
        try {
            List<BuyerDTO> buyers = adminService.getAllUsersByRole("BUYER").stream().filter(UserDetail::getActive)
                    .map( BuyerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(buyers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch buyers: " + e.getMessage()));
        }
    }

    @GetMapping("/buyers/all")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getAllBuyersIncludingDeleted() {
        try {
            List<BuyerDTO> buyers = adminService.getAllUsersByRole("BUYER")
                    .stream().map(BuyerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(buyers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch all buyers: " + e.getMessage()));
        }
    }

    @GetMapping("/buyers/deleted")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getDeletedBuyers() {
        try {
            List<BuyerDTO> deletedBuyers = adminService.getAllUsersByRole("BUYER")
                    .stream().filter(user -> !user.getActive())
                    .map(BuyerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(deletedBuyers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch deleted buyers: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<UserDetail>> getUser(@PathVariable String username) {
        try {
            UserDetail user = userService.findByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable String username,
                                                          @Valid @RequestBody UserUpdateDTO userDto,
                                                          BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append(" "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errors.toString()));
        }

        try {
            userService.updateUserProfile(userDto, username);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error updating user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String username) {
        try {
            int result = userService.deletUserByName(username);
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error deleting user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/email/{email}/activate")
    public ResponseEntity<?> activateUserByEmail(@PathVariable String email) {
        userService.activateByEmail(email);
        return ResponseEntity.ok("User activated successfully");
    }
}