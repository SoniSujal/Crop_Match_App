package com.cropMatch.controller.admin;

import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.buyerDTO.BuyerDTO;
import com.cropMatch.dto.farmerDTO.FarmerDTO;
import com.cropMatch.dto.common.UserUpdateDTO;
import com.cropMatch.model.common.UserPrincipal;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.service.admin.AdminService;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/{userId}/farmers")
    public ResponseEntity<ApiResponse<List<FarmerDTO>>> getAllActiveFarmers(@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

        try {
            List<FarmerDTO> farmers = adminService.getAllUsersByRole("FARMER").stream()
                    .filter(UserDetail::getActive)
                    .map( FarmerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(farmers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch farmers: " + e.getMessage()));
        }
    }

    @GetMapping("/{userId}/farmers/all")
    public ResponseEntity<ApiResponse<List<FarmerDTO>>> getAllFarmersIncludingDeleted(@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

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

    @GetMapping("/{userId}/farmers/deleted")
    public ResponseEntity<ApiResponse<List<FarmerDTO>>> getDeletedFarmers(@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

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

    @GetMapping("/{userId}/buyers")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getAllActiveBuyers(@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

        try {
            List<BuyerDTO> buyers = adminService.getAllUsersByRole("BUYER").stream()
                    .filter(UserDetail::getActive)
                    .map( BuyerDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(buyers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to fetch buyers: " + e.getMessage()));
        }
    }

    @GetMapping("/{userId}/buyers/all")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getAllBuyersIncludingDeleted(@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

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

    @GetMapping("/{userId}/buyers/deleted")
    public ResponseEntity<ApiResponse<List<BuyerDTO>>> getDeletedBuyers(@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

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

    @GetMapping("/{userId}/user/{username}")
    public ResponseEntity<ApiResponse<UserDetail>> getUser(@PathVariable String username,@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

        try {
            UserDetail user = userService.findByUserEmail(username);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}/user/{username}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable String username,
                                                          @Valid @RequestBody UserUpdateDTO userDto,
                                                          BindingResult result,
                                                          @PathVariable Integer userId,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

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

    @DeleteMapping("/{userId}/user/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String username,@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

        try {
            int result = userService.deletUserByEmail(username);
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

    @PutMapping("/{userId}/users/email/{email}/activate")
    public ResponseEntity<?> activateUserByEmail(@PathVariable String email,@PathVariable Integer userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (!userService.findByUserEmail(userPrincipal.getUsername()).getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User unauthorized"));
        }

        userService.activateByEmail(email);
        return ResponseEntity.ok("User activated successfully");
    }
}