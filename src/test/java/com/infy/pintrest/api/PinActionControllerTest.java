package com.infy.pintrest.api;

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
import org.springframework.test.web.servlet.MockMvc;

import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.service.PinInteractionService;

@WebMvcTest(PinActionController.class)
public class PinActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PinInteractionService pinInteractionService;

    private Pin testPin;
    private List<Pin> savedPins;

    @BeforeEach
    void setUp() {
        testPin = new Pin();
        testPin.setId(1);
        testPin.setTitle("Test Pin");
        testPin.setDescription("Test Description");
        testPin.setImageUrl("/uploads/pins/test.jpg");

        Pin pin2 = new Pin();
        pin2.setId(2);
        pin2.setTitle("Second Pin");

        savedPins = Arrays.asList(testPin, pin2);
    }

    @Nested
    @DisplayName("POST /api/pins/save - Save Pin Tests")
    class SavePinTests {

        @Test
        @DisplayName("Should save pin successfully")
        void savePin_Success() throws Exception {
            when(pinInteractionService.savePin(1, 1)).thenReturn("Pin saved successfully");

            mockMvc.perform(post("/api/pins/save")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Pin saved successfully"));
        }

        @Test
        @DisplayName("Should return message when already saved")
        void savePin_AlreadySaved_Success() throws Exception {
            when(pinInteractionService.savePin(1, 1)).thenReturn("Pin already saved");

            mockMvc.perform(post("/api/pins/save")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Pin already saved"));
        }

        @Test
        @DisplayName("Should fail without userId parameter")
        void savePin_MissingUserId_Failure() throws Exception {
            mockMvc.perform(post("/api/pins/save")
                    .param("pinId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail without pinId parameter")
        void savePin_MissingPinId_Failure() throws Exception {
            mockMvc.perform(post("/api/pins/save")
                    .param("userId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail with invalid userId")
        void savePin_InvalidUserId_Failure() throws Exception {
            mockMvc.perform(post("/api/pins/save")
                    .param("userId", "invalid")
                    .param("pinId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail with invalid pinId")
        void savePin_InvalidPinId_Failure() throws Exception {
            mockMvc.perform(post("/api/pins/save")
                    .param("userId", "1")
                    .param("pinId", "invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/pins/isSaved - Is Pin Saved Tests")
    class IsSavedTests {

        @Test
        @DisplayName("Should return true when saved")
        void isSaved_True_Success() throws Exception {
            when(pinInteractionService.isSaved(1, 1)).thenReturn(true);

            mockMvc.perform(get("/api/pins/isSaved")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false when not saved")
        void isSaved_False_Success() throws Exception {
            when(pinInteractionService.isSaved(1, 1)).thenReturn(false);

            mockMvc.perform(get("/api/pins/isSaved")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should fail without userId parameter")
        void isSaved_MissingUserId_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/isSaved")
                    .param("pinId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail without pinId parameter")
        void isSaved_MissingPinId_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/isSaved")
                    .param("userId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail with invalid userId")
        void isSaved_InvalidUserId_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/isSaved")
                    .param("userId", "invalid")
                    .param("pinId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail with invalid pinId")
        void isSaved_InvalidPinId_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/isSaved")
                    .param("userId", "1")
                    .param("pinId", "invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/pins/saved - Get Saved Pins Tests")
    class GetSavedPinsTests {

        @Test
        @DisplayName("Should get saved pins successfully")
        void getSavedPins_Success() throws Exception {
            when(pinInteractionService.getSavedPins(1)).thenReturn(savedPins);

            mockMvc.perform(get("/api/pins/saved")
                    .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Pin"));
        }

        @Test
        @DisplayName("Should return empty list when no saved pins")
        void getSavedPins_Empty_Success() throws Exception {
            when(pinInteractionService.getSavedPins(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/pins/saved")
                    .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail without userId parameter")
        void getSavedPins_MissingUserId_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/saved"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should fail with invalid userId")
        void getSavedPins_InvalidUserId_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/saved")
                    .param("userId", "invalid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle user with many saved pins")
        void getSavedPins_ManyPins_Success() throws Exception {
            when(pinInteractionService.getSavedPins(1)).thenReturn(savedPins);

            mockMvc.perform(get("/api/pins/saved")
                    .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Invalid Request Tests")
    class InvalidRequestTests {

        @Test
        @DisplayName("Should fail save with GET method")
        void savePin_GetMethod_Failure() throws Exception {
            mockMvc.perform(get("/api/pins/save")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail isSaved with POST method")
        void isSaved_PostMethod_Failure() throws Exception {
            mockMvc.perform(post("/api/pins/isSaved")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail saved with POST method")
        void getSavedPins_PostMethod_Failure() throws Exception {
            mockMvc.perform(post("/api/pins/saved")
                    .param("userId", "1"))
                    .andExpect(status().isMethodNotAllowed());
        }
    }
}
