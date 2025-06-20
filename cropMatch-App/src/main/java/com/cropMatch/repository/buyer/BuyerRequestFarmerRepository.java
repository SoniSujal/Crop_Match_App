package com.cropMatch.repository.buyer;

import com.cropMatch.enums.ResponseStatus;
import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.buyer.BuyerRequestFarmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuyerRequestFarmerRepository extends JpaRepository<BuyerRequestFarmer, Integer> {
    List<BuyerRequestFarmer> findByFarmerId(Integer farmerId);
    boolean existsByBuyerRequestAndFarmerId(BuyerRequest request, Integer farmerId);

    @Query("SELECT brf FROM BuyerRequestFarmer brf WHERE brf.buyerRequest.buyerId = :buyerId AND brf.farmerStatus IN :status")
    List<BuyerRequestFarmer> findByBuyerIdAndStatus(@Param("buyerId") Integer buyerId, @Param("status") List<ResponseStatus> status);

    List<BuyerRequestFarmer> findByBuyerRequest_Id(Integer buyerRequestId);

}
