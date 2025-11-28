package com.infy.pintrest.service;

import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.LoginDto;
import com.infy.pintrest.dto.UserDto;
import com.infy.pintrest.exception.InfyPintrestException;

public interface AuthService {

    Integer registerUser(UserDto user) throws InfyPintrestException;

    Integer loginUser(LoginDto user) throws InfyPintrestException;

    String updateProfilePic(Integer userId, MultipartFile file) throws InfyPintrestException;

    UserDto getUserDetails(Integer userId) throws InfyPintrestException;

}
