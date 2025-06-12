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

}
