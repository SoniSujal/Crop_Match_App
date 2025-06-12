package com.cropMatch.service.user;

import com.cropMatch.dto.authDTO.RegistrationDTO;
import com.cropMatch.dto.common.UserUpdateDTO;
import com.cropMatch.enums.UserRoles;
import com.cropMatch.exception.AccountDeletedException;
import com.cropMatch.exception.EmailAlreadyExistsException;
import com.cropMatch.exception.EmailNotFoundException;
import com.cropMatch.exception.InvalidUserTypeException;
import com.cropMatch.model.admin.Category;
import com.cropMatch.model.buyer.BuyerPreference;
import com.cropMatch.model.user.UserDetail;
import com.cropMatch.model.user.UserType;
import com.cropMatch.model.user.UserTypeMapping;
import com.cropMatch.repository.buyer.BuyerPreferencesRepository;
import com.cropMatch.repository.category.CategoryRepository;
import com.cropMatch.repository.common.UserDetailRepository;
import com.cropMatch.repository.user.UserTypeRepository;
import com.cropMatch.service.buyer.BuyerService;
import jakarta.transaction.Transactional;
import liquibase.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDetailRepository userDetailRepository;

    private final UserTypeRepository userTypeRepository;

    private final PasswordEncoder passwordEncoder;

    private final BuyerPreferencesRepository buyerPreferencesRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void register(RegistrationDTO registrationDto) {
        UserDetail userDetail = userDetailRepository.findByEmail(registrationDto.getEmail()).orElse(null);

        if (userDetail != null && !userDetail.getActive()) {
            throw new AccountDeletedException("Account Deleted By Admin");
        }

        if (userDetail != null) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserRoles userTypeName = registrationDto.getUserType();

        UserDetail user = new UserDetail();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setMobile(registrationDto.getMobile());
        user.setEmail(registrationDto.getEmail());
        user.setPincode(registrationDto.getPincode());
        user.setCountry(registrationDto.getCountry());
        user.setCreatedOn(LocalDateTime.now());
        user.setActive(true);

        UserType userType = userTypeRepository.findByName(String.valueOf(userTypeName))
                .orElseThrow(() -> new InvalidUserTypeException("Invalid user type"));

        UserTypeMapping mapping = new UserTypeMapping();
        mapping.setUser(user);
        mapping.setUserType(userType);
        mapping.setCreatedOn(LocalDateTime.now());

        user.getUserTypes().add(mapping);
        userDetailRepository.save(user);

        if (userTypeName.equals("BUYER")) {
            List<Integer> categoryIds = registrationDto.getPreferenceCategoryIds();

            if (CollectionUtils.isEmpty(categoryIds) || categoryIds.size() < 1 || categoryIds.size() > 5) {
                throw new RuntimeException("Buyer must select between 1 and 5 preferences.");
            }

            List<BuyerPreference> preferences = new ArrayList<>();
            for (Integer catId : categoryIds) {
                Category category = categoryRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + catId));
                preferences.add(new BuyerPreference(user.getId(), category));
            }

            buyerPreferencesRepository.saveAll(preferences);
        }


    }

    @Override
    @Transactional
    public void updateUserProfile(UserUpdateDTO dto, String username) {

        UserDetail user = userDetailRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
                .orElseThrow(() -> new UsernameNotFoundException("User not Found"));
    }

    @Override
    public String findByUsernameUsingId(Integer id){
        return userDetailRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found")).getUsername();
    }

    @Override
    public  UserDetail findByUserEmail(String email){
        return  userDetailRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not Found"));
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

    public int activateByEmail(String email) {
        return userDetailRepository.ActiveUserByName(email);
    }
}
