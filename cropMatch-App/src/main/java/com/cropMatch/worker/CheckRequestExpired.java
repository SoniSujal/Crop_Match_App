package com.cropMatch.worker;

import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.service.BuyerRequest.BuyerRequestService;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckRequestExpired {

    private BuyerRequestService buyerRequestService;

    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void runDailyTask() {
        List<BuyerRequest> allBuyerRequests = buyerRequestService.getAllBuyerRequest();
        LocalDate today = LocalDate.now();

        for (BuyerRequest buyerRequest : allBuyerRequests) {
            boolean isExpired = false;

            LocalDate createdOn = buyerRequest.getCreatedOn().toLocalDate(); // Assuming it's stored as LocalDateTime
            LocalDate updatedOn = buyerRequest.getUpdatedOn().toLocalDate();
            LocalDate needByDate = buyerRequest.getNeedByDate();

            LocalDate expectedExpiryDate;

            if (needByDate == null) {
                if (updatedOn.isEqual(createdOn)) {
                    expectedExpiryDate = createdOn.plusDays(5);
                } else {
                    expectedExpiryDate = updatedOn.plusDays(5);
                }

                if (today.isAfter(expectedExpiryDate)) {
                    isExpired = true;
                }

            } else {
                if (needByDate.isBefore(today) && updatedOn.isBefore(today)) {
                    isExpired = true;
                }
            }

            if (buyerRequest.getIsExpired() != isExpired) {
                buyerRequest.setIsExpired(isExpired);
                buyerRequestService.saveRequest(buyerRequest);
            }
        }
    }
}
