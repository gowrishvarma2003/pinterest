package com.infy.pintrest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.pintrest.dto.BusinessProfileViewDTO;
import com.infy.pintrest.exception.GlobalExceptionHandler;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.BusinessService;

@WebMvcTest(BusinessProfileController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
public class BusinessProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @Autowired
    private ObjectMapper objectMapper;

    private BusinessProfileViewDTO testBusinessProfile;
    private List<BusinessProfileViewDTO> businessProfileList;

    @BeforeEach
    void setUp() {
        testBusinessProfile = new BusinessProfileViewDTO();
        testBusinessProfile.setBusinessProfileId(1);
        testBusinessProfile.setBusinessName("Test Business");
        testBusinessProfile.setWebsiteUrl("https://testbusiness.com");
        testBusinessProfile.setDescription("A test business profile");
        testBusinessProfile.setCategory("Technology");
        testBusinessProfile.setVerified(true);
        testBusinessProfile.setOwnerId(1);
        testBusinessProfile.setOwnerUsername("testuser");
        testBusinessProfile.setOwnerFullName("Test User");
        testBusinessProfile.setTotalBoards(5);
        testBusinessProfile.setTotalPins(50);
        testBusinessProfile.setFollowerCount(100);

        BusinessProfileViewDTO profile2 = new BusinessProfileViewDTO();
        profile2.setBusinessProfileId(2);
        profile2.setBusinessName("Second Business");
        profile2.setWebsiteUrl("https://secondbusiness.com");
        profile2.setOwnerId(2);

        businessProfileList = Arrays.asList(testBusinessProfile, profile2);
    }

    @Nested
    @DisplayName("GET /api/business/{userId} - Get Business Profile Tests")
    class GetBusinessProfileTests {

        @Test
        @DisplayName("Should get business profile successfully")
        void getBusinessProfile_Success() throws Exception {
            when(businessService.getBusinessProfileByUserId(1)).thenReturn(testBusinessProfile);

            mockMvc.perform(get("/api/business/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.businessProfileId").value(1))
                    .andExpect(jsonPath("$.businessName").value("Test Business"))
                    .andExpect(jsonPath("$.websiteUrl").value("https://testbusiness.com"))
                    .andExpect(jsonPath("$.verified").value(true));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getBusinessProfile_UserNotFound_Failure() throws Exception {
            when(businessService.getBusinessProfileByUserId(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/api/business/999"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when user has no business profile")
        void getBusinessProfile_NoBusinessProfile_Failure() throws Exception {
            when(businessService.getBusinessProfileByUserId(1))
                    .thenThrow(new InfyPintrestException("Business profile not found"));

            mockMvc.perform(get("/api/business/1"))
                    .andExpect(status().isInternalServerError());
        }

    }

    @Nested
    @DisplayName("GET /api/business - Get All Business Profiles Tests")
    class GetAllBusinessProfilesTests {

        @Test
        @DisplayName("Should get all business profiles successfully")
        void getAllBusinessProfiles_Success() throws Exception {
            when(businessService.getAllBusinessProfiles()).thenReturn(businessProfileList);

            mockMvc.perform(get("/api/business"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].businessName").value("Test Business"))
                    .andExpect(jsonPath("$[1].businessName").value("Second Business"));
        }

        @Test
        @DisplayName("Should return empty list when no business profiles")
        void getAllBusinessProfiles_Empty_Success() throws Exception {
            when(businessService.getAllBusinessProfiles()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/business"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return single business profile")
        void getAllBusinessProfiles_SingleProfile_Success() throws Exception {
            when(businessService.getAllBusinessProfiles())
                    .thenReturn(Arrays.asList(testBusinessProfile));

            mockMvc.perform(get("/api/business"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].businessName").value("Test Business"));
        }
    }

    @Nested
    @DisplayName("POST /api/business/convert/{userId} - Convert to Business Tests")
    class ConvertToBusinessTests {

        @Test
        @DisplayName("Should convert to business successfully")
        void convertToBusiness_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("businessName", "New Business");
            request.put("websiteUrl", "https://newbusiness.com");
            request.put("category", "Technology");

            when(businessService.convertToBusiness(eq(1), eq("New Business"), 
                    eq("https://newbusiness.com"), eq("Technology")))
                    .thenReturn(testBusinessProfile);

            mockMvc.perform(post("/api/business/convert/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.businessName").value("Test Business"));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void convertToBusiness_UserNotFound_Failure() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("businessName", "New Business");
            request.put("websiteUrl", "https://newbusiness.com");
            request.put("category", "Technology");

            when(businessService.convertToBusiness(eq(999), any(), any(), any()))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(post("/api/business/convert/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when user already has business profile")
        void convertToBusiness_AlreadyBusiness_Failure() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("businessName", "New Business");
            request.put("websiteUrl", "https://newbusiness.com");
            request.put("category", "Technology");

            when(businessService.convertToBusiness(eq(1), any(), any(), any()))
                    .thenThrow(new InfyPintrestException("User already has a business profile"));

            mockMvc.perform(post("/api/business/convert/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with missing business name")
        void convertToBusiness_MissingBusinessName_Failure() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("websiteUrl", "https://newbusiness.com");
            request.put("category", "Technology");

            when(businessService.convertToBusiness(eq(1), eq(null), any(), any()))
                    .thenThrow(new InfyPintrestException("Business name is required"));

            mockMvc.perform(post("/api/business/convert/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid category")
        void convertToBusiness_InvalidCategory_Failure() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("businessName", "New Business");
            request.put("websiteUrl", "https://newbusiness.com");
            request.put("category", "InvalidCategory");

            when(businessService.convertToBusiness(eq(1), any(), any(), eq("InvalidCategory")))
                    .thenThrow(new InfyPintrestException("Invalid category"));

            mockMvc.perform(post("/api/business/convert/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should convert with optional fields null")
        void convertToBusiness_OptionalFieldsNull_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("businessName", "New Business");

            BusinessProfileViewDTO minimalProfile = new BusinessProfileViewDTO();
            minimalProfile.setBusinessProfileId(1);
            minimalProfile.setBusinessName("New Business");

            when(businessService.convertToBusiness(eq(1), eq("New Business"), eq(null), eq(null)))
                    .thenReturn(minimalProfile);

            mockMvc.perform(post("/api/business/convert/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.businessName").value("New Business"));
        }
    }
}
