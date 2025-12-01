package com.infy.pintrest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.AuthResponseDTO;
import com.infy.pintrest.dto.LoginDto;
import com.infy.pintrest.dto.UserDto;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.AuthService;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/auth")
public class AuthApi {

    @Autowired
    private AuthService authService;

    @Autowired
    Environment env;

    @PostMapping("/registeruser")
    public ResponseEntity<AuthResponseDTO> registerUser(@RequestBody UserDto user) throws InfyPintrestException {
        AuthResponseDTO response = authService.registerUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/loginuser")
    public ResponseEntity<AuthResponseDTO> loginuser(@RequestBody LoginDto user) throws InfyPintrestException {
        AuthResponseDTO response = authService.loginUser(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer userId) throws InfyPintrestException {
        UserDto user = authService.getUserDetails(userId);
        return new ResponseEntity<UserDto>(user, HttpStatus.OK);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) throws InfyPintrestException {
        UserDto updatedUser = authService.updateUser(userId, userDto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping(value = "/user/{userId}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfilePicture(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) throws InfyPintrestException {
        String savedPath = authService.updateProfilePic(userId, file);
        return new ResponseEntity<>(savedPath, HttpStatus.OK);
    }
}
