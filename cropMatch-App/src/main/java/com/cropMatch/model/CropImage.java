package com.cropMatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "crop_id")
    private Crop crop;


}