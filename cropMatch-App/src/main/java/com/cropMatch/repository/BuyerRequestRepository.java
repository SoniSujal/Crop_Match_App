package com.cropMatch.repository;

import com.cropMatch.model.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Integer> {}

