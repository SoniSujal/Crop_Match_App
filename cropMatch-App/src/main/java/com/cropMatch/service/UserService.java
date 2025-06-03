package com.cropMatch.service;

import com.cropMatch.dto.UserRegistrationDto;
import com.cropMatch.dto.UserUpdateDto;
import com.cropMatch.model.UserDetail;

import java.util.Optional;

public interface UserService {

    void register(UserRegistrationDto registrationDto);
    void updateUserProfile(UserUpdateDto dto, String username);
    UserDetail findByUsername(String username);
    Optional<UserDetail> authenticate(String userEmail, String password);

}
