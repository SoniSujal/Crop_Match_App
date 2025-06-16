package com.cropMatch.controller.crop;

import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.farmerDTO.CropDTO;
import com.cropMatch.service.crop.CropService;
import com.cropMatch.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@CrossOrigin(origins = "*")
@Slf4j
public class CropController {

    private final CropService cropService;

    private final UserService userService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> addCrop(Principal principal, @Valid @RequestPart("cropDTO") CropDTO cropDTO, @RequestPart("images") List<MultipartFile> images) {
        String username = principal.getName();
        Integer farmerId = userService.findByUserEmail(username).getId();
        cropService.saveCropWithImages(cropDTO, images, farmerId);
        return new ResponseEntity<>(ApiResponse.success("Crop Added Successfully"), HttpStatus.OK);
    }
}
