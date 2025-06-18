package com.cropMatch.repository.crop;

import com.cropMatch.model.admin.Category;
import com.cropMatch.model.farmer.AvailableCrops;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableCropsRepository extends JpaRepository<AvailableCrops, Integer> {

    boolean existsByCropNameAndCategory(String cropName, Category category);
}
