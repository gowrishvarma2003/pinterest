package com.infy.pintrest.service;

import java.util.Optional;
import java.util.UUID;
import java.time.Duration;
import javax.management.RuntimeErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import com.infy.pinterest.enums.AccountType;
import com.infy.pintrest.dto.AuthResponseDTO;
import com.infy.pintrest.dto.LoginDto;
import com.infy.pintrest.dto.UserDto;
import com.infy.pintrest.entity.BusinessProfile;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.UserRepository;
import com.infy.pintrest.utility.HelperFunctions;
import com.infy.pintrest.utility.JwtUtil;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 1;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String root = System.getProperty("user.dir");
    private String uploadDir = root + "/uploads/profile";

    HelperFunctions hp = new HelperFunctions();

    public AuthResponseDTO registerUser(UserDto userDto) throws InfyPintrestException {
        // Check if email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("EMAIL_EXISTS");
            errorResponse.setMessage("An account with this email already exists");
            return errorResponse;
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("USERNAME_EXISTS");
            errorResponse.setMessage("This username is already taken");
            return errorResponse;
        }
        
        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());
        newUser.setPassword(userDto.getPassword()); // Need to hash and save.
        newUser.setUsername(userDto.getUsername());
        newUser.setMobile(userDto.getMobile());
        newUser.setBio(userDto.getBio());
        newUser.setAccountType(userDto.getAccountType());

        User u = userRepository.save(newUser);

        if (userDto.getAccountType() == AccountType.BUSINESS) {
            BusinessProfile bp = new BusinessProfile();
            bp.setBusinessName(userDto.getBusinessName());
            bp.setWebsiteUrl(userDto.getWebsiteUrl());
            bp.setDescription(userDto.getDescription());
            bp.setUser(u);

            u.setBusinessProfile(bp);
            userRepository.save(u);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(u.getId(), u.getEmail(), u.getName());

        // Create response with token and user details
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUserId(u.getId());
        response.setName(u.getName());
        response.setEmail(u.getEmail());
        response.setUsername(u.getUsername());
        response.setMobile(u.getMobile());
        response.setBio(u.getBio());
        response.setProfilePath(u.getProfilePath());
        response.setAccountType(u.getAccountType());
        response.setMessage("User registered successfully");

        if (u.getBusinessProfile() != null) {
            response.setBusinessName(u.getBusinessProfile().getBusinessName());
            response.setWebsiteUrl(u.getBusinessProfile().getWebsiteUrl());
            response.setDescription(u.getBusinessProfile().getDescription());
        }

        return response;
    }

    public AuthResponseDTO loginUser(@RequestBody LoginDto user) throws InfyPintrestException {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        
        // Email not found
        if (userData.isEmpty()) {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("EMAIL_NOT_FOUND");
            errorResponse.setMessage("No account found with this email address");
            return errorResponse;
        }
        
        User data = userData.get();
        
        // Check if account is locked
        if (data.getLockoutEndTime() != null && LocalDateTime.now().isBefore(data.getLockoutEndTime())) {
            long remainingSeconds = Duration.between(LocalDateTime.now(), data.getLockoutEndTime()).getSeconds();
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("ACCOUNT_LOCKED");
            errorResponse.setMessage("Account is locked. Please try again in " + remainingSeconds + " seconds");
            errorResponse.setLockoutRemainingSeconds(remainingSeconds);
            return errorResponse;
        }
        
        // Wrong password
        if (!Objects.equals(data.getPassword(), user.getPassword())) {
            // Increment failed attempts
            int failedAttempts = data.getFailedLoginAttempts() + 1;
            data.setFailedLoginAttempts(failedAttempts);
            
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                // Lock the account
                data.setLockoutEndTime(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                userRepository.save(data);
                
                errorResponse.setErrorCode("ACCOUNT_LOCKED");
                errorResponse.setMessage("Too many failed attempts. Account locked for 1 minute");
                errorResponse.setLockoutRemainingSeconds(60L);
            } else {
                userRepository.save(data);
                int remainingAttempts = MAX_FAILED_ATTEMPTS - failedAttempts;
                errorResponse.setErrorCode("WRONG_PASSWORD");
                errorResponse.setMessage("Incorrect password. " + remainingAttempts + " attempt(s) remaining");
            }
            
            return errorResponse;
        }
        
        // Successful login - reset failed attempts and lockout
        data.setFailedLoginAttempts(0);
        data.setLockoutEndTime(null);
        userRepository.save(data);

        // Generate JWT token
        String token = jwtUtil.generateToken(data.getId(), data.getEmail(), data.getName());

        // Create response with token and user details
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUserId(data.getId());
        response.setName(data.getName());
        response.setEmail(data.getEmail());
        response.setUsername(data.getUsername());
        response.setMobile(data.getMobile());
        response.setBio(data.getBio());
        response.setProfilePath(data.getProfilePath());
        response.setAccountType(data.getAccountType());
        response.setMessage("Login successful");

        if (data.getBusinessProfile() != null) {
            response.setBusinessName(data.getBusinessProfile().getBusinessName());
            response.setWebsiteUrl(data.getBusinessProfile().getWebsiteUrl());
            response.setDescription(data.getBusinessProfile().getDescription());
        }

        return response;
    }

    @Override
    public String updateProfilePic(Integer userId, MultipartFile file) throws InfyPintrestException {
        Optional<User> userData = userRepository.findById(userId);
        if (userData.isEmpty()) {
            new InfyPintrestException("Auth.UserNotExists");
        }
        User data = userData.get();

        String savedPath = hp.saveFile(file, uploadDir);
        
        // Convert absolute path to relative URL path for serving via web
        // savedPath is like: C:/Users/.../uploads/profile/uuid.jpg
        // We need: /uploads/profile/uuid.jpg
        String relativePath = savedPath.replace(root, "").replace("\\", "/");
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }
        
        data.setProfilePath(relativePath);
        userRepository.save(data);

        return relativePath;
    }

    @Override
    public UserDto getUserDetails(Integer userId) throws InfyPintrestException {
        Optional<User> userData = userRepository.findById(userId);
        if (userData.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }
        return mapToDto(userData.get());
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setUsername(user.getUsername());
        dto.setMobile(user.getMobile());
        dto.setBio(user.getBio());
        dto.setProfilePath(user.getProfilePath());
        dto.setAccountType(user.getAccountType());

        BusinessProfile bp = user.getBusinessProfile();
        if (bp != null) {
            dto.setBusinessName(bp.getBusinessName());
            dto.setWebsiteUrl(bp.getWebsiteUrl());
            dto.setDescription(bp.getDescription());
        }

        return dto;
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) throws InfyPintrestException {
        Optional<User> userData = userRepository.findById(userId);
        if (userData.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }
        
        User user = userData.get();
        
        // Update only allowed fields (not email and username)
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getBio() != null) {
            user.setBio(userDto.getBio());
        }
        if (userDto.getMobile() != null) {
            user.setMobile(userDto.getMobile());
        }
        
        // Update business profile if user is a business account
        if (user.getAccountType() == AccountType.BUSINESS) {
            BusinessProfile bp = user.getBusinessProfile();
            if (bp == null) {
                bp = new BusinessProfile();
                bp.setUser(user);
                user.setBusinessProfile(bp);
            }
            
            if (userDto.getBusinessName() != null) {
                bp.setBusinessName(userDto.getBusinessName());
            }
            if (userDto.getWebsiteUrl() != null) {
                bp.setWebsiteUrl(userDto.getWebsiteUrl());
            }
            if (userDto.getDescription() != null) {
                bp.setDescription(userDto.getDescription());
            }
        }
        
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }
}
