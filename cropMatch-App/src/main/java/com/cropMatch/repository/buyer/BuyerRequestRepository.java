package com.cropMatch.repository.buyer;

import com.cropMatch.model.buyer.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Integer> {
    List<BuyerRequest> findByBuyerId(Integer buyerId);
}

