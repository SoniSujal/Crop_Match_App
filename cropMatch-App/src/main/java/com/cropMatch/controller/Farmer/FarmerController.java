package com.cropMatch.controller.Farmer;

import com.cropMatch.dto.ApiResponse;
import com.cropMatch.dto.CropDTO;
import com.cropMatch.service.farmer.FarmerService;
import com.cropMatch.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FarmerController {

    @Autowired
    private final FarmerService farmerService;

    @Autowired
    private final UserService userService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> addCrop(Principal principal, @Valid @RequestPart("cropDTO") CropDTO cropDTO, @RequestPart("images") List<MultipartFile> images) {
        String username = principal.getName();
        Integer farmerId = userService.findByUsername(username).getId();
        farmerService.saveCropWithImages(cropDTO, images, farmerId);
        return new ResponseEntity<>(ApiResponse.success("Crop Added Successfully"), HttpStatus.OK);
    }
}
