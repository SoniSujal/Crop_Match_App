package com.cropMatch.service.admin;

import com.cropMatch.model.user.UserDetail;

import java.util.List;


public interface AdminService {

    public List<UserDetail> getAllUsersByRole(String role);

}
