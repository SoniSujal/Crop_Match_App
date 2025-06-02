package com.cropMatch.repository;

import com.cropMatch.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
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
}