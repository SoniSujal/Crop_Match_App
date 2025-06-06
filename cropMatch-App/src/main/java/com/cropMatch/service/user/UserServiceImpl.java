    package com.cropMatch.service.user;

    import com.cropMatch.dto.UserRegistrationDTO;
    import com.cropMatch.dto.UserUpdateDTO;
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
    public class UserServiceImpl implements UserService{

        @Autowired
        private final UserDetailRepository userDetailRepository;

        @Autowired
        private final UserTypeRepository userTypeRepository;

        @Autowired
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void register(UserRegistrationDTO registrationDto) {
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
            user.setPincode(registrationDto.getPincode());
            user.setCountry(registrationDto.getCountry());
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

        @Override
        @Transactional
        public void updateUserProfile(UserUpdateDTO dto, String username) {

            UserDetail user = userDetailRepository.findByUsername(username).orElseThrow(() -> new BusinessException("User not found"));

            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setMobile(dto.getMobile());
            user.setPincode(dto.getPincode());
            user.setCountry(dto.getCountry());

            userDetailRepository.save(user);
        }

        @Override
        public  UserDetail findByUsername(String username){
            return  userDetailRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("User not Found"));
        }

        @Override
        public  UserDetail findByUserEmail(String email){
            return  userDetailRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException("User not Found"));
        }

        @Override
        public int deletUserByName(String username) {
            return userDetailRepository.softDeleteUserByName(username);
        }


        @Override
        public Optional<UserDetail> authenticate(String userEmail, String password) {
            return userDetailRepository.findByEmail(userEmail)
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                    .filter(UserDetail::getActive);
        }
    }
