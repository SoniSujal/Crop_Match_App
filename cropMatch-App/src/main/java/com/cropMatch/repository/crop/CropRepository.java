package com.cropMatch.repository.crop;

import com.cropMatch.model.farmer.Crop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Integer> {

    @Query("SELECT DISTINCT c FROM Crop c LEFT JOIN FETCH c.images WHERE c.category.id IN :categoryIds AND c.status = true")
    List<Crop> findByCategoryIdIn(@Param("categoryIds") List<Integer> categoryIds, Sort sort);

    @Query("SELECT DISTINCT c FROM Crop c LEFT JOIN FETCH c.images WHERE c.category.id NOT IN :categoryIds AND c.status = true")
    List<Crop> findByCategoryIdNotIn(@Param("categoryIds") List<Integer> categoryIds, Sort sort);

    // Optional: fallback for when there are no preferences
    List<Crop> findAll(Sort sort);
}
