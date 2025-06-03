package com.cropMatch.service;

import com.cropMatch.model.UserDetail;
import com.cropMatch.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService{

    private UserDetailRepository userDetailRepository;

    @Override
    public List<UserDetail> getAllFarmers(String farmer) {
        System.out.println(userDetailRepository.findAllByUserTypeName("FARMER").get(0).getEmail());
        return userDetailRepository.findAllByUserTypeName("FARMER");
    }

    @Override
    public List<UserDetail> getAllBuyers(String buyer) {
        return userDetailRepository.findAllByUserTypeName("BUYER");
    }
}
