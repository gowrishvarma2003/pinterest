package com.infy.pintrest.dto;

import com.infy.pinterest.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private Integer userId;
    private String name;
    private String email;
    private String username;
    private String mobile;
    private String bio;
    private String profilePath;
    private AccountType accountType;
    private String businessName;
    private String websiteUrl;
    private String description;
    private String message;
    private String errorCode;
    private Long lockoutRemainingSeconds;
}
