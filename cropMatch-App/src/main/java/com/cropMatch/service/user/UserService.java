package com.cropMatch.service.user;

import com.cropMatch.dto.authDTO.RegistrationDTO;
import com.cropMatch.dto.common.UserUpdateDTO;
import com.cropMatch.model.user.UserDetail;

import java.util.Optional;

public interface UserService {

    void register(RegistrationDTO registrationDto);
    void updateUserProfile(UserUpdateDTO dto, String username);
    UserDetail findByUsername(String username);
    Optional<UserDetail> authenticate(String userEmail, String password);
    public  UserDetail findByUserEmail(String email);
    public int deletUserByName(String username);
    public int activateByEmail(String email);
}
