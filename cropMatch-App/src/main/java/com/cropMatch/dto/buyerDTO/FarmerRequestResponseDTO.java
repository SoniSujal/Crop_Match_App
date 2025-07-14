package com.cropMatch.dto.buyerDTO;

import com.cropMatch.dto.farmerDTO.FarmerDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FarmerRequestResponseDTO {
        private int id;
        private String status;
        private LocalDateTime respondedOn;
        private BuyerRequestResponseDTO buyerRequest;
        private FarmerDTO farmer;
        private String offeredQuality;
        private String offeredProducedWay;
}
