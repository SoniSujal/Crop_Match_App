package com.cropMatch.service.buyer;

import com.cropMatch.dto.buyerDTO.BuyerRequestDTO;
import com.cropMatch.dto.buyerDTO.BuyerRequestResponseDTO;
import com.cropMatch.enums.CropUnit;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.admin.Category;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerRequestRepository;
import com.cropMatch.repository.category.CategoryRepository;
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
    public List<BuyerRequestResponseDTO> getAllRequests() {
        return requestRepository.findAll().stream().map( BuyerRequestResponseDTO::new).toList();
    }

    public record UnitDTO(String name, String displayName) {}
}


