package com.infy.pintrest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.pinterest.enums.AccountType;
import com.infy.pintrest.dto.AuthResponseDTO;
import com.infy.pintrest.dto.LoginDto;
import com.infy.pintrest.dto.UserDto;
import com.infy.pintrest.exception.GlobalExceptionHandler;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.AuthService;

@WebMvcTest(controllers = AuthApi.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
public class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUser;
    private LoginDto testLogin;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        testUser = new UserDto();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setUsername("testuser");
        testUser.setMobile("1234567890");
        testUser.setBio("Test bio");
        testUser.setAccountType(AccountType.USER);

        testLogin = new LoginDto("test@example.com", "password123");

        authResponse = new AuthResponseDTO();
        authResponse.setToken("jwt-token-here");
        authResponse.setUserId(1);
        authResponse.setName("Test User");
        authResponse.setEmail("test@example.com");
        authResponse.setUsername("testuser");
        authResponse.setAccountType(AccountType.USER);
        authResponse.setMessage("Success");
    }

    @Nested
    @DisplayName("POST /auth/registeruser - Register User Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should register user successfully")
        void registerUser_Success() throws Exception {
            when(authService.registerUser(any(UserDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/registeruser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.token").value("jwt-token-here"));
        }

        @Test
        @DisplayName("Should return error when email already exists")
        void registerUser_EmailExists_Failure() throws Exception {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("EMAIL_EXISTS");
            errorResponse.setMessage("An account with this email already exists");
            when(authService.registerUser(any(UserDto.class))).thenReturn(errorResponse);

            mockMvc.perform(post("/auth/registeruser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.errorCode").value("EMAIL_EXISTS"));
        }

        @Test
        @DisplayName("Should fail registration with invalid request body")
        void registerUser_InvalidBody_Failure() throws Exception {
            when(authService.registerUser(any(UserDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/registeruser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should return error when username already exists")
        void registerUser_UsernameExists_Failure() throws Exception {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("USERNAME_EXISTS");
            errorResponse.setMessage("This username is already taken");
            when(authService.registerUser(any(UserDto.class))).thenReturn(errorResponse);

            mockMvc.perform(post("/auth/registeruser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.errorCode").value("USERNAME_EXISTS"));
        }
    }

    @Nested
    @DisplayName("POST /auth/loginuser - Login User Tests")
    class LoginUserTests {

        @Test
        @DisplayName("Should login user successfully")
        void loginUser_Success() throws Exception {
            when(authService.loginUser(any(LoginDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/loginuser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testLogin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-here"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("Should return error for invalid credentials")
        void loginUser_InvalidCredentials_Failure() throws Exception {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("WRONG_PASSWORD");
            errorResponse.setMessage("Incorrect password. 2 attempt(s) remaining");
            when(authService.loginUser(any(LoginDto.class))).thenReturn(errorResponse);

            mockMvc.perform(post("/auth/loginuser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testLogin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errorCode").value("WRONG_PASSWORD"));
        }

        @Test
        @DisplayName("Should return error when user not found")
        void loginUser_UserNotFound_Failure() throws Exception {
            AuthResponseDTO errorResponse = new AuthResponseDTO();
            errorResponse.setErrorCode("EMAIL_NOT_FOUND");
            errorResponse.setMessage("No account found with this email address");
            when(authService.loginUser(any(LoginDto.class))).thenReturn(errorResponse);

            mockMvc.perform(post("/auth/loginuser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testLogin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errorCode").value("EMAIL_NOT_FOUND"));
        }

        @Test
        @DisplayName("Should fail login when account is locked")
        void loginUser_AccountLocked_Failure() throws Exception {
            AuthResponseDTO lockedResponse = new AuthResponseDTO();
            lockedResponse.setErrorCode("ACCOUNT_LOCKED");
            lockedResponse.setLockoutRemainingSeconds(300L);
            when(authService.loginUser(any(LoginDto.class))).thenReturn(lockedResponse);

            mockMvc.perform(post("/auth/loginuser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testLogin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errorCode").value("ACCOUNT_LOCKED"));
        }
    }

    @Nested
    @DisplayName("GET /auth/user/{userId} - Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user details successfully")
        void getUser_Success() throws Exception {
            when(authService.getUserDetails(1)).thenReturn(testUser);

            mockMvc.perform(get("/auth/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getUser_NotFound_Failure() throws Exception {
            when(authService.getUserDetails(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/auth/user/999"))
                    .andExpect(status().isInternalServerError());
        }

    }

    @Nested
    @DisplayName("PUT /auth/user/{userId} - Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void updateUser_Success() throws Exception {
            UserDto updatedUser = new UserDto();
            updatedUser.setUserId(1);
            updatedUser.setName("Updated Name");
            updatedUser.setEmail("test@example.com");
            updatedUser.setBio("Updated bio");

            when(authService.updateUser(eq(1), any(UserDto.class))).thenReturn(updatedUser);

            mockMvc.perform(put("/auth/user/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.bio").value("Updated bio"));
        }

        @Test
        @DisplayName("Should fail update when user not found")
        void updateUser_NotFound_Failure() throws Exception {
            when(authService.updateUser(eq(999), any(UserDto.class)))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(put("/auth/user/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail update with duplicate email")
        void updateUser_DuplicateEmail_Failure() throws Exception {
            when(authService.updateUser(eq(1), any(UserDto.class)))
                    .thenThrow(new InfyPintrestException("Email already in use"));

            mockMvc.perform(put("/auth/user/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /auth/user/{userId}/profile-picture - Update Profile Picture Tests")
    class UpdateProfilePictureTests {

        @Test
        @DisplayName("Should update profile picture successfully")
        void updateProfilePicture_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "profile.jpg", "image/jpeg", "test image content".getBytes());

            when(authService.updateProfilePic(eq(1), any())).thenReturn("/uploads/profile/profile.jpg");

            mockMvc.perform(multipart("/auth/user/1/profile-picture")
                    .file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().string("/uploads/profile/profile.jpg"));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void updateProfilePicture_UserNotFound_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "profile.jpg", "image/jpeg", "test image content".getBytes());

            when(authService.updateProfilePic(eq(999), any()))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(multipart("/auth/user/999/profile-picture")
                    .file(file))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid file type")
        void updateProfilePicture_InvalidFileType_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "document.pdf", "application/pdf", "pdf content".getBytes());

            when(authService.updateProfilePic(eq(1), any()))
                    .thenThrow(new InfyPintrestException("Invalid file type"));

            mockMvc.perform(multipart("/auth/user/1/profile-picture")
                    .file(file))
                    .andExpect(status().isInternalServerError());
        }
    }
}
