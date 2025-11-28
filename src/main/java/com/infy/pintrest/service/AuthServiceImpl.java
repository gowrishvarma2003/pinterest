package com.infy.pintrest.service;

import java.util.Optional;
import java.util.UUID;
import javax.management.RuntimeErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import com.infy.pinterest.enums.AccountType;
import com.infy.pintrest.dto.LoginDto;
import com.infy.pintrest.dto.UserDto;
import com.infy.pintrest.entity.BusinessProfile;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.UserRepository;
import com.infy.pintrest.utility.HelperFunctions;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    private String root = System.getProperty("user.dir");
    private String uploadDir = root + "/uploads/profile";

    HelperFunctions hp = new HelperFunctions();

    public Integer registerUser(UserDto userDto) {
        Optional<User> userData = userRepository.findByEmail(userDto.getEmail());
        if (userData.isPresent()) {
            new InfyPintrestException("Auth.UserExists");
        }
        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());
        newUser.setPassword(userDto.getPassword()); // Need to hash and save.
        newUser.setFullname(userDto.getFullname());
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

        return u.getId();
    }

    public Integer loginUser(@RequestBody LoginDto user) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if (userData.isEmpty()) {
            new InfyPintrestException("Auth.UserNotExists");
        }
        User data = userData.get();
        if (!Objects.equals(data.getPassword(), user.getPassword())) {
            new InfyPintrestException("Auth.WrondPassword");
        }
        return data.getId();
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
        dto.setFullname(user.getFullname());
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
}
