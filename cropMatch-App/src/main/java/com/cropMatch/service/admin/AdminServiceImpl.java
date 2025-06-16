package com.cropMatch.service.admin;

import com.cropMatch.model.user.UserDetail;
import com.cropMatch.repository.common.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService{

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public List<UserDetail> getAllUsersByRole(String role) {
        return userDetailRepository.findAllByUserTypeName(role);
    }
}
