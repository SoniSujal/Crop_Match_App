package com.cropMatch.repository.buyer;

import com.cropMatch.model.buyer.BuyerRequest;
import com.cropMatch.model.buyer.BuyerRequestFarmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyerRequestFarmerRepository extends JpaRepository<BuyerRequestFarmer, Integer> {
    List<BuyerRequestFarmer> findByFarmerId(Integer farmerId);
    boolean existsByBuyerRequestAndFarmerId(BuyerRequest request, Integer farmerId);
}
