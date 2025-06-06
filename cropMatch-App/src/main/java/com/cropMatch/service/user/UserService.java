package com.cropMatch.service.user;

import com.cropMatch.dto.UserRegistrationDTO;
import com.cropMatch.dto.UserUpdateDTO;
import com.cropMatch.model.UserDetail;

import java.util.Optional;

public interface UserService {

    void register(UserRegistrationDTO registrationDto);
    void updateUserProfile(UserUpdateDTO dto, String username);
    UserDetail findByUsername(String username);
    Optional<UserDetail> authenticate(String userEmail, String password);
    public  UserDetail findByUserEmail(String email);
    public int deletUserByName(String username);

}
