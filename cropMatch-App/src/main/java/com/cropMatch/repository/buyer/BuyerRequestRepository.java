package com.cropMatch.repository.buyer;

import com.cropMatch.model.buyer.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Integer> {}

