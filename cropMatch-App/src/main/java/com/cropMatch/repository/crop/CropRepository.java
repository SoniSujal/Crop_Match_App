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

    @Query(
            value = """
            SELECT DISTINCT c.* FROM crop c
            WHERE c.status = true
            ORDER BY
                CASE WHEN c.category_id IN (:categoryIds) THEN 0 ELSE 1 END,
                c.created_on DESC
            """,
            countQuery = "SELECT COUNT(*) FROM crops WHERE status = true",
            nativeQuery = true
    )
    Page<Crop> findAllSortedByCategoryPreferenceNative(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Crop c LEFT JOIN FETCH c.images WHERE c.id IN :ids")
    List<Crop> findAllWithImagesByIds(@Param("ids") List<Integer> ids);
}
