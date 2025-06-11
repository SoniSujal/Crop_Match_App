package com.cropMatch.repository.buyer;

import com.cropMatch.model.buyer.BuyerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerPreferencesRepository extends JpaRepository<BuyerPreference, Long> {
    void deleteByBuyerId(Integer buyerId);
    List<BuyerPreference> findByBuyerId(Integer buyerId);
}

