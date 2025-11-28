package com.infy.pintrest.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infy.pinterest.enums.AccountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String email;
    private String password;
    private String fullname;
    private String mobile;
    private String bio;
    private String profilePath;

    @Enumerated(EnumType.STRING)
    private AccountType accountType = AccountType.USER;

    private int failedLoginAttemps = 0;

    private LocalDateTime lastFailedAttempt;

    @OneToMany(mappedBy = "owner")
    @JsonIgnoreProperties({"owner", "pins"})
    private List<Board> boards;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user", "board"})
    private List<Pin> pins;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private BusinessProfile businessProfile;

}
