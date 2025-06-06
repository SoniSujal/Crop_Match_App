package com.cropMatch.service.user.buyer;

import com.cropMatch.dto.BuyerRequestDTO;
import com.cropMatch.enums.CropUnit;
import com.cropMatch.model.BuyerRequest;
import com.cropMatch.model.Category;
import com.cropMatch.model.UserDetail;
import com.cropMatch.repository.BuyerRequestRepository;
import com.cropMatch.repository.CategoryRepository;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {


    private final BuyerRequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public BuyerRequest createRequest(BuyerRequestDTO dto, String username) {
        UserDetail buyer = userService.findByUsername(username);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not Found!"));

        BuyerRequest request = new BuyerRequest();
        request.setCropName(dto.getCropName());
        request.setQuantity(dto.getQuantity());
        request.setUnit(CropUnit.valueOf(dto.getUnit()));
        request.setRegion(dto.getRegion());
        request.setExpectedPrice(dto.getExpectedPrice());
        request.setCategory(category);
        request.setBuyerId(buyer.getId());
        request.setCreatedOn(LocalDateTime.now());

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
    public List<BuyerRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public record UnitDTO(String name, String displayName) {}
}


