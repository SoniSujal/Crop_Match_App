package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.enums.CropUnit;
import com.cropMatch.model.buyer.BuyerPreference;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.admin.Category;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerPreferencesRepository;
import com.cropMatch.repository.buyer.BuyerRequestRepository;
import com.cropMatch.repository.category.CategoryRepository;
import com.cropMatch.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

    private final BuyerRequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final BuyerPreferencesRepository buyerPreferencesRepository;
    private final UserService userService;

    @Override
    public BuyerRequest createRequest(BuyerRequestDTO buyerRequestDTO, String username) {
        UserDetail buyer = userService.findByUserEmail(username);

        Category category = categoryRepository.findById(buyerRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not Found!"));

        BuyerRequest request = new BuyerRequest(buyerRequestDTO, category, buyer.getId());
        return requestRepository.save(request);
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    public List<UnitDTO> getAllUnits() {
        List<UnitDTO> units = new ArrayList<>();

        for (CropUnit unit : CropUnit.values()) {
            units.add(new UnitDTO(unit.name(), unit.getDisplayName()));
        }

        return units;
    }

    @Override
    public List<Integer> getBuyerPreferenceCategoryIds(Integer userId) {
        return buyerPreferencesRepository.findByBuyerId(userId)
                .stream()
                .map(pref -> pref.getCategory().getId())
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public boolean updateBuyerPreferences(String username, List<Integer> newCategoryIds) {

        if (newCategoryIds == null || newCategoryIds.isEmpty()) {
            return false;
        }

//        UserDetail buyer = userService.findByUsername(username);
        UserDetail buyer = userService.findByUserEmail(username);
        Integer buyerId = buyer.getId();

        List<BuyerPreference> currentPreferences = buyerPreferencesRepository.findByBuyerId(buyerId);
        Set<Integer> currentCategoryIds = new HashSet<>();
        for (BuyerPreference pref : currentPreferences) {
            currentCategoryIds.add(pref.getCategory().getId());
        }


        List<BuyerPreference> preferencesToDelete = new ArrayList<>();
        for (BuyerPreference pref : currentPreferences) {
            if (!newCategoryIds.contains(pref.getCategory().getId())) {
                preferencesToDelete.add(pref);
            }
        }

        List<Integer> categoriesToAddIds = new ArrayList<>();
        for (Integer newId : newCategoryIds) {
            if (!currentCategoryIds.contains(newId)) {
                categoriesToAddIds.add(newId);
            }
        }

        boolean changesMade = false;

        if (!preferencesToDelete.isEmpty()) {
            buyerPreferencesRepository.deleteAll(preferencesToDelete);
            changesMade = true;
        }

        if (!categoriesToAddIds.isEmpty()) {
            List<BuyerPreference> preferencesToAdd = new ArrayList<>();
            for (Integer catId : categoriesToAddIds) {
                Category category = categoryRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + catId));
                preferencesToAdd.add(new BuyerPreference(buyerId, category));
            }
            buyerPreferencesRepository.saveAll(preferencesToAdd);
            changesMade = true;
        }

        return changesMade;
    }

    @Override
    public List<Integer> getBuyerPreferences(String username) {
        UserDetail buyer = userService.findByUserEmail(username);
        return buyerPreferencesRepository.findByBuyerId(buyer.getId()).stream()
                .map(pref -> pref.getCategory().getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<BuyerRequestResponseDTO> getAllRequests(String username) {
        UserDetail buyer = userService.findByUserEmail(username);
        return requestRepository.findByBuyerId(buyer.getId()).stream()
                .map( BuyerRequestResponseDTO::new)
                .toList();
    }

    public record UnitDTO(String name, String displayName) {}
}