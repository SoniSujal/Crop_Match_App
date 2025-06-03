package com.cropMatch.service;

import com.cropMatch.model.UserDetail;

import java.util.List;

public interface AdminService {

    public List<UserDetail> getAllFarmers(String farmer);

    public List<UserDetail> getAllBuyers(String buyer);
}
