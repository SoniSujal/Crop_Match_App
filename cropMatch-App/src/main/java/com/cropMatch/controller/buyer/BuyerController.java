package com.cropMatch.controller.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.service.buyer.BuyerService;
import com.cropMatch.service.crop.CropService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("api/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {

    private final BuyerService buyerService;

    private final CropService cropService;

    private final UserDetailRepository  userDetailRepository;

    @GetMapping
    public String showBuyersPage() {
        return "buyers";
    }

    @PostMapping("/requests/create")
    public ResponseEntity<?> createRequest(@RequestBody BuyerRequestDTO dto, Principal principal){
        BuyerRequest saved = buyerService.createRequest(dto, principal.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getAllRequests(Principal principal) {
        return ResponseEntity.ok(buyerService.getAllRequests(principal.getName()));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getActiveCategories(){
        return ResponseEntity.ok(buyerService.getActiveCategories());
    }

    @GetMapping("/units")
    public ResponseEntity<?> getAllUnits() {
        return ResponseEntity.ok(buyerService.getAllUnits());
    }

    @GetMapping("{email}/recommendations")
    public ResponseEntity<Page<RecommendationDTO>> getRecommendationsByCategories(
            @PathVariable String email,
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "createdOn") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        UserDetail buyer = userDetailRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<Integer> categoryIds = buyerService.getBuyerPreferenceCategoryIds(buyer.getId());

        Page<RecommendationDTO> recommendations = cropService.getRecommendedCropsDTO(categoryIds, pageNo, pageSize, sortBy,  sortDir);

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/recommendations/{cropId}")
    public ResponseEntity<ApiResponse<RecommendationDTO>> getCropDetailsByCropId(@PathVariable Integer cropId) {
        return ResponseEntity.ok(cropService.getCropById(cropId));
    }

    @GetMapping("/{email}/recommendation/top")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> getTopRecommendations(@PathVariable String email) {
        List<RecommendationDTO> topRecords = cropService.getTopRecommendations(email);
        return ResponseEntity.ok(ApiResponse.success(topRecords));
    }

    @GetMapping("/preferences")
    public ResponseEntity<List<Integer>> getBuyerPreferences(Principal principal) {
        List<Integer> preferenceIds = buyerService.getBuyerPreferences(principal.getName());
        return ResponseEntity.ok(preferenceIds);
    }

    @PostMapping("/preferences")
    public ResponseEntity<?> updatePreferences(@RequestBody List<Integer> categoryIds,Principal principal){
        boolean updated = buyerService.updateBuyerPreferences(principal.getName(),categoryIds);
        if (updated){
            return ResponseEntity.ok("Preference updated successfully!");
        }else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No New Preferences were added.");
        }
    }
}
