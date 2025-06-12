package com.cropMatch.controller.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.dto.buyerDTO.RecommendationDTO;
import com.cropMatch.dto.responseDTO.PagedResponse;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.service.buyer.BuyerService;
import com.cropMatch.service.crop.CropService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<PagedResponse<RecommendationDTO>> getRecommendationsByCategories(@PathVariable String email, @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
                                                                                           @RequestParam(name = "pageSize", defaultValue = "2") int pageSize,
                                                                                           @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                                           @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        UserDetail buyer = userDetailRepository.findByEmail(email).orElse(null);
        List<Integer> categoryIds = buyerService.getBuyerPreferenceCategoryIds(buyer.getId());
        return ResponseEntity.ok(cropService.recommedCropsDetailsBaseCategory(categoryIds,pageNo, pageSize, sortBy, sortDir));
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
