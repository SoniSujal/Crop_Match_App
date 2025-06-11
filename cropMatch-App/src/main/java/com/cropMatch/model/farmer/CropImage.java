package com.cropMatch.model.farmer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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
    @JsonBackReference
    private Crop crop;
}