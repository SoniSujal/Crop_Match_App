package com.cropMatch.repository.common;

import com.cropMatch.model.user.UserDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {
    Optional<UserDetail> findByUsername(String username);
    Optional<UserDetail> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserDetail u JOIN u.userTypes ut WHERE ut.userType.name = :userTypeName")
    List<UserDetail> findAllByUserTypeName(@Param("userTypeName") String userTypeName);

    @Modifying
    @Transactional
    @Query("UPDATE UserDetail u SET u.active = false WHERE u.username = :userName")
    int softDeleteUserByName(@Param("userName") String userName);

    @Modifying
    @Transactional
    @Query("UPDATE UserDetail u SET u.active = true WHERE u.email = :userName")
    int ActiveUserByName(@Param("userName") String userName);
}