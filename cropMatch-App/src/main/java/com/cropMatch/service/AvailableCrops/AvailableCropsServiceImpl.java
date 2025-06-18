package com.cropMatch.service.AvailableCrops;

import com.cropMatch.model.farmer.AvailableCrops;
import com.cropMatch.repository.crop.AvailableCropsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableCropsServiceImpl implements AvailableCropsService{

    @Autowired
    private AvailableCropsRepository availableCropsRepository;

    @Override
    public List<AvailableCrops> getAllActiveCateRealtedCrops() {
        return availableCropsRepository.findAll();
    }
}
