package com.infy.pintrest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> registerUser(@RequestBody UserDto user) throws InfyPintrestException {
        Integer userId = authService.registerUser(user);
        String msg = "User Registered Succesfully" + userId;
        return new ResponseEntity<String>(msg, HttpStatus.OK);
    }

    @PostMapping("/loginuser")
    public ResponseEntity<String> loginuser(@RequestBody LoginDto user) throws InfyPintrestException {
        Integer userId = authService.loginUser(user);
        String msg = "User loged in Succesfully" + userId;
        return new ResponseEntity<String>(msg, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer userId) throws InfyPintrestException {
        UserDto user = authService.getUserDetails(userId);
        return new ResponseEntity<UserDto>(user, HttpStatus.OK);
    }
}
