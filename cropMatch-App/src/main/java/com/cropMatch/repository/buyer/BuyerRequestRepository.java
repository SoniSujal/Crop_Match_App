package com.cropMatch.repository.buyer;

import com.cropMatch.dto.buyerDTO.CropMatchProjection;
import com.cropMatch.model.buyer.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Integer> {
    List<BuyerRequest> findByBuyerId(Integer buyerId);

    @Query(value = """
    SELECT c.crop_id AS id,
           c.name,
           c.region,
           c.price,
           c.selling_quantity AS sellingQuantity,
           c.selling_unit AS sellingUnit,
           c.produced_way AS producedWay,
           c.quality,
           c.created_by AS createdBy,
           (CASE WHEN (:region IS NOT NULL AND c.region = :region) THEN 2 ELSE 0 END +
            CASE WHEN (:quality IS NOT NULL AND c.quality = :quality) THEN 1 ELSE 0 END +
            CASE WHEN (:producedWay IS NOT NULL AND c.produced_way = :producedWay) THEN 1 ELSE 0 END +
            CASE WHEN (:expectedPrice IS NOT NULL AND c.normalization_price_per_KG <= :expectedPrice) THEN 3 ELSE 0 END +
            CASE WHEN (:requiredQuantity IS NOT NULL AND c.normalized_quantity >= :requiredQuantity) THEN 3 ELSE 0 END
           ) AS matchScore
    FROM crop c
    WHERE c.name = :cropName
      AND c.status = true
    ORDER BY matchScore DESC
""", nativeQuery = true)
    List<CropMatchProjection> findMatchingCropsWithFlexibleCriteria(
            @Param("cropName") String cropName,
            @Param("region") String region,
            @Param("quality") String quality,
            @Param("producedWay") String producedWay,
            @Param("expectedPrice") Double expectedPrice,
            @Param("requiredQuantity") Double requiredQuantity
    );
}

