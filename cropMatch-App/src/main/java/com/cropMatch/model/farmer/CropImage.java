package com.cropMatch.model.farmer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "crop_id")
    @JsonBackReference
    private Crop crop;

    public CropImage(Crop crop, MultipartFile image,String imagePath) throws IOException {
        this.imageName = image.getOriginalFilename();
        this.imageType = image.getContentType();
        this.imageData = image.getBytes();
        this.imagePath = imagePath;
        this.crop = crop;
    }
}