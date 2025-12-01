package com.infy.pintrest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.pintrest.dto.SponsoredPinDTO;
import com.infy.pintrest.service.SponsoredPinService;

@WebMvcTest(SponsoredPinServiceController.class)
public class SponsoredPinServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SponsoredPinService sponsoredPinService;

    @Autowired
    private ObjectMapper objectMapper;

    private SponsoredPinDTO testSponsoredPin;
    private List<SponsoredPinDTO> sponsoredPinList;

    @BeforeEach
    void setUp() {
        testSponsoredPin = new SponsoredPinDTO();
        testSponsoredPin.setId(1L);
        testSponsoredPin.setTitle("Test Sponsored Pin");
        testSponsoredPin.setDescription("Test Ad Description");
        testSponsoredPin.setImageUrl("/uploads/ads/image.jpg");
        testSponsoredPin.setTargetUrl("https://example.com");
        testSponsoredPin.setAdvertiseName("Test Advertiser");
        testSponsoredPin.setKeywords("test, keywords");
        testSponsoredPin.setBudget(1000.0);
        testSponsoredPin.setActive(true);

        SponsoredPinDTO pin2 = new SponsoredPinDTO();
        pin2.setId(2L);
        pin2.setTitle("Second Sponsored Pin");
        pin2.setAdvertiseName("Second Advertiser");
        pin2.setActive(true);

        sponsoredPinList = Arrays.asList(testSponsoredPin, pin2);
    }

    @Nested
    @DisplayName("POST /api/ads/create - Create Sponsored Pin Tests")
    class CreateSponsoredPinTests {

        @Test
        @DisplayName("Should create sponsored pin successfully")
        void createSponsoredPin_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Sponsored Pin"))
                    .andExpect(jsonPath("$.advertiseName").value("Test Advertiser"));
        }

        @Test
        @DisplayName("Should create sponsored pin without file")
        void createSponsoredPin_WithoutFile_Success() throws Exception {
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(adPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("Test Sponsored Pin"));
        }

        @Test
        @DisplayName("Should fail without ad data")
        void createSponsoredPin_NoAdData_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create sponsored pin with all fields")
        void createSponsoredPin_AllFields_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.budget").value(1000.0))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.targetUrl").value("https://example.com"));
        }

        @Test
        @DisplayName("Should handle large budget values")
        void createSponsoredPin_LargeBudget_Success() throws Exception {
            testSponsoredPin.setBudget(1000000.0);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.budget").value(1000000.0));
        }

        @Test
        @DisplayName("Should create inactive sponsored pin")
        void createSponsoredPin_Inactive_Success() throws Exception {
            testSponsoredPin.setActive(false);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.active").value(false));
        }
    }

    @Nested
    @DisplayName("Invalid Request Tests")
    class InvalidRequestTests {

        @Test
        @DisplayName("Should fail with GET method on create")
        void createSponsoredPin_GetMethod_Failure() throws Exception {
            mockMvc.perform(get("/api/ads/create"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with PUT method on create")
        void createSponsoredPin_PutMethod_Failure() throws Exception {
            mockMvc.perform(put("/api/ads/create"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with DELETE method on create")
        void createSponsoredPin_DeleteMethod_Failure() throws Exception {
            mockMvc.perform(delete("/api/ads/create"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with invalid JSON")
        void createSponsoredPin_InvalidJson_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", "invalid json".getBytes());

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty title")
        void createSponsoredPin_EmptyTitle_Success() throws Exception {
            testSponsoredPin.setTitle("");

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should handle null budget")
        void createSponsoredPin_NullBudget_Success() throws Exception {
            testSponsoredPin.setBudget(null);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should handle special characters in keywords")
        void createSponsoredPin_SpecialCharactersKeywords_Success() throws Exception {
            testSponsoredPin.setKeywords("test@#$, special&chars");

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should handle long description")
        void createSponsoredPin_LongDescription_Success() throws Exception {
            String longDescription = "A".repeat(1000);
            testSponsoredPin.setDescription(longDescription);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should handle zero budget")
        void createSponsoredPin_ZeroBudget_Success() throws Exception {
            testSponsoredPin.setBudget(0.0);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "ad.jpg", "image/jpeg", "ad image content".getBytes());
            MockMultipartFile adPart = new MockMultipartFile(
                    "ad", "", "application/json", objectMapper.writeValueAsBytes(testSponsoredPin));

            when(sponsoredPinService.createSponsoredPin(any(SponsoredPinDTO.class), any()))
                    .thenReturn(testSponsoredPin);

            mockMvc.perform(multipart("/api/ads/create")
                    .file(file)
                    .file(adPart))
                    .andExpect(status().isCreated());
        }
    }
}
