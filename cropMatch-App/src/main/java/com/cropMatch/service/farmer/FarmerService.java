package com.cropMatch.service.farmer;

import com.cropMatch.dto.CropDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface FarmerService {

    void saveCropWithImages(@Valid CropDTO cropDTO, List<MultipartFile> images, Integer farmerId);

    public String uploadFileWithData(Integer farmerId,List<MultipartFile> file) throws IOException;

}
