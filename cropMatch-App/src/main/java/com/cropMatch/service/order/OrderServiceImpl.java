package com.cropMatch.service.order;

import com.cropMatch.dto.buyerDTO.OrderDetailDTO;
import com.cropMatch.enums.ResponseStatus;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.buyer.BuyerRequestFarmer;
import com.cropMatch.model.farmer.Crop;
import com.cropMatch.model.farmer.CropImage;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.buyer.BuyerRequestFarmerRepository;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.repository.crop.CropRepository;
import com.cropMatch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final CropRepository cropRepository;

    private final UserService userService;

    private final BuyerRequestFarmerRepository buyerRequestFarmerRepository;

    @Override
    public OrderDetailDTO prepareOrderFromCrop(Integer cropId, String username){
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        Integer farmerId = crop.getCreatedBy();   // get farmer ID from response
        String farmerUsername = userService.findByUsernameUsingId(farmerId);

        return new OrderDetailDTO(
                cropId,null,
                crop.getName(),
                crop.getCategory().getName(),
                farmerUsername,
                crop.getRegion(),
                crop.getQuality().name(),
                crop.getProducedWay().name(),
                crop.getPrice(),
                crop.getStockQuantity(),
                crop.getSellingUnit(),
                crop.getImages().stream().findFirst().map(CropImage::getImagePath).orElse(null)
        );
    }

    @Override
    public OrderDetailDTO prepareOrderFromRequest(Integer requestResponseId, String username){
        BuyerRequestFarmer response = buyerRequestFarmerRepository.findById(requestResponseId)
                .orElseThrow(() -> new IllegalArgumentException("Request Response not found"));

        if (response.getFarmerStatus() != ResponseStatus.SELECTED) {
            throw new IllegalArgumentException("cannot create order from unaccepted response");
        }

        BuyerRequest request = response.getBuyerRequest();
        Crop crop = response.getCrop();

        Integer farmerId = response.getFarmerId();
        String farmerUsername = userService.findByUsernameUsingId(farmerId);

        return new OrderDetailDTO(
                 crop.getId(),response.getId(),
                 request.getMatchedCropName(),
                 request.getCategory().getName(),
                 farmerUsername,
                 crop.getRegion(),
                 response.getCrop().getQuality().name(),
                 response.getCrop().getProducedWay().name(),
                 request.getExpectedPrice(),
                 crop.getStockQuantity(),
                 request.getUnit().name(),
                 crop.getImages().stream().findFirst().map(CropImage::getImagePath).orElse(null)
         );
    }


}
