package com.cropMatch.repository.crop;

import com.cropMatch.model.farmer.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Integer> {

    @Query("SELECT c FROM Crop c JOIN FETCH c.images WHERE c.category.id = :categoryId AND c.status = true")
    List<Crop> findCropsWithImagesByCategoryId(@Param("categoryId") int categoryId);

    @Query(value = """
    WITH filtered AS (
        SELECT c.*
        FROM crop c
        JOIN category cat ON c.category_id = cat.id
        WHERE c.status = true
          AND cat.id IN (:categoryIds)
          AND c.expire_month > DATE_FORMAT(NOW(), '%Y-%m')
        ORDER BY 
          (CASE WHEN c.region = :buyerRegion THEN 1 ELSE 0 END) DESC,
          (CASE c.quality WHEN 'BEST' THEN 3 WHEN 'GOOD' THEN 2 ELSE 1 END) DESC,
          c.price ASC,
          (CASE c.produced_way WHEN 'ORGANIC' THEN 3 WHEN 'MIXED' THEN 2 ELSE 1 END) DESC,
          c.quantity DESC
        LIMIT 10
    ),
    crop_name_count AS (
        SELECT COUNT(DISTINCT name) AS distinct_names FROM filtered
    )
    SELECT *
    FROM (
        SELECT f.*,
               ROW_NUMBER() OVER (
                 PARTITION BY f.name
                 ORDER BY 
                   (CASE WHEN f.region = :buyerRegion THEN 1 ELSE 0 END) DESC,
                   (CASE f.quality WHEN 'BEST' THEN 3 WHEN 'GOOD' THEN 2 ELSE 1 END) DESC,
                   f.price ASC,
                   (CASE f.produced_way WHEN 'ORGANIC' THEN 3 WHEN 'MIXED' THEN 2 ELSE 1 END) DESC,
                   f.quantity DESC
               ) AS rn,
               (SELECT distinct_names FROM crop_name_count) AS distinct_names
        FROM filtered f
    ) t
    WHERE 
      (distinct_names > 1 AND rn = 1)
      OR
      (distinct_names = 1)
    ORDER BY 
      (CASE WHEN region = :buyerRegion THEN 1 ELSE 0 END) DESC,
      (CASE quality WHEN 'BEST' THEN 3 WHEN 'GOOD' THEN 2 ELSE 1 END) DESC,
      price ASC,
      (CASE produced_way WHEN 'ORGANIC' THEN 3 WHEN 'MIXED' THEN 2 ELSE 1 END) DESC,
      quantity DESC
    LIMIT 3
    """, nativeQuery = true)
    List<Crop> findTopRecommendations(
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("buyerRegion") String buyerRegion
    );



}
