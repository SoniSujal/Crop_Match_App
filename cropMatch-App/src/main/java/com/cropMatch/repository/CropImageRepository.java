package com.cropMatch.repository;

import com.cropMatch.model.CropImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CropImageRepository extends JpaRepository<CropImage, Integer> {
}
