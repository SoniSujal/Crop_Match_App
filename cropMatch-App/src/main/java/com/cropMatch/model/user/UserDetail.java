package com.cropMatch.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_detail")
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "country")
    private String country;

    @Column(name = "region")
    private String region;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on",updatable = false,insertable = false)
    private LocalDateTime updatedOn;

    @Column(name = "is_active")
    private Boolean active = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserTypeMapping> userTypes = new HashSet<>();
}