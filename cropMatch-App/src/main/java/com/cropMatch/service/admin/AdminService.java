package com.cropMatch.service.admin;

import com.cropMatch.model.UserDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminService {

    public List<UserDetail> getAllUsersByRole(String role);

}
