package com.cropMatch.service;

import com.cropMatch.dto.UserRegistrationDto;
import com.cropMatch.exception.BusinessException;
import com.cropMatch.model.UserDetail;
import com.cropMatch.model.UserType;
import com.cropMatch.model.UserTypeMapping;
import com.cropMatch.repository.UserDetailRepository;
import com.cropMatch.repository.UserTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserDetailRepository userDetailRepository;
    @Autowired
    private final UserTypeRepository userTypeRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        if (userDetailRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userDetailRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        String userTypeName = registrationDto.getUserType().toUpperCase();

        UserDetail user = new UserDetail();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setMobile(registrationDto.getMobile());
        user.setEmail(registrationDto.getEmail());
        user.setCreatedOn(LocalDateTime.now());
        user.setActive(true);

        UserType userType = userTypeRepository.findByName(userTypeName)
                .orElseThrow(() -> new BusinessException("Invalid user type"));

        UserTypeMapping mapping = new UserTypeMapping();
        mapping.setUser(user);
        mapping.setUserType(userType);
        mapping.setCreatedOn(LocalDateTime.now());

        user.getUserTypes().add(mapping);
        userDetailRepository.save(user);
    }

    public Optional<UserDetail> authenticate(String username, String password) {
        return userDetailRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .filter(UserDetail::getActive);
    }
}
