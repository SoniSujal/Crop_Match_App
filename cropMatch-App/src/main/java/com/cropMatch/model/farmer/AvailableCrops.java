package com.cropMatch.model.farmer;

import com.cropMatch.model.admin.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "available_crops",
        uniqueConstraints = @UniqueConstraint(columnNames = {"crop_name", "category_id"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableCrops {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
