package com.infy.pintrest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.pinterest.enums.PinStatus;
import com.infy.pintrest.dto.PinDTO;
import com.infy.pintrest.dto.PinViewDTO;
import com.infy.pintrest.exception.GlobalExceptionHandler;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.PinService;

@WebMvcTest(PinController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
public class PinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PinService pinService;

    @Autowired
    private ObjectMapper objectMapper;

    private PinDTO testPinDTO;
    private PinViewDTO testPinViewDTO;
    private List<PinViewDTO> pinList;

    @BeforeEach
    void setUp() {
        testPinDTO = new PinDTO();
        testPinDTO.setId(1);
        testPinDTO.setTitle("Test Pin");
        testPinDTO.setDescription("Test Description");
        testPinDTO.setImageUrl("/uploads/pins/test.jpg");
        testPinDTO.setUserId(1);
        testPinDTO.setBoardId(1);
        testPinDTO.setStatus(PinStatus.PUBLISHED);
        testPinDTO.setPrivate(false);

        testPinViewDTO = new PinViewDTO();
        testPinViewDTO.setId(1);
        testPinViewDTO.setTitle("Test Pin");
        testPinViewDTO.setDescription("Test Description");
        testPinViewDTO.setImageUrl("/uploads/pins/test.jpg");
        testPinViewDTO.setUserId(1);
        testPinViewDTO.setBoardId(1);
        testPinViewDTO.setBoardTitle("Test Board");
        testPinViewDTO.setUserName("testuser");
        testPinViewDTO.setUserFullname("Test User");
        testPinViewDTO.setStatus(PinStatus.PUBLISHED);
        testPinViewDTO.setCreatedAt(LocalDateTime.now());

        PinViewDTO pin2 = new PinViewDTO();
        pin2.setId(2);
        pin2.setTitle("Second Pin");
        pin2.setUserId(1);

        pinList = Arrays.asList(testPinViewDTO, pin2);
    }

    @Nested
    @DisplayName("POST /pins/createpin - Create Pin Tests")
    class CreatePinTests {

        @Test
        @DisplayName("Should create pin successfully")
        void createPin_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "image.jpg", "image/jpeg", "image content".getBytes());
            MockMultipartFile pinPart = new MockMultipartFile(
                    "pin", "", "application/json", objectMapper.writeValueAsBytes(testPinDTO));

            when(pinService.createPin(any(PinDTO.class), any())).thenReturn(testPinDTO);

            mockMvc.perform(multipart("/pins/createpin")
                    .file(file)
                    .file(pinPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Pin"));
        }

        @Test
        @DisplayName("Should create pin without file")
        void createPin_WithoutFile_Success() throws Exception {
            MockMultipartFile pinPart = new MockMultipartFile(
                    "pin", "", "application/json", objectMapper.writeValueAsBytes(testPinDTO));

            when(pinService.createPin(any(PinDTO.class), any())).thenReturn(testPinDTO);

            mockMvc.perform(multipart("/pins/createpin")
                    .file(pinPart))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should fail when user not found")
        void createPin_UserNotFound_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "image.jpg", "image/jpeg", "image content".getBytes());
            MockMultipartFile pinPart = new MockMultipartFile(
                    "pin", "", "application/json", objectMapper.writeValueAsBytes(testPinDTO));

            when(pinService.createPin(any(PinDTO.class), any()))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(multipart("/pins/createpin")
                    .file(file)
                    .file(pinPart))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when board not found")
        void createPin_BoardNotFound_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "image.jpg", "image/jpeg", "image content".getBytes());
            MockMultipartFile pinPart = new MockMultipartFile(
                    "pin", "", "application/json", objectMapper.writeValueAsBytes(testPinDTO));

            when(pinService.createPin(any(PinDTO.class), any()))
                    .thenThrow(new InfyPintrestException("Board not found"));

            mockMvc.perform(multipart("/pins/createpin")
                    .file(file)
                    .file(pinPart))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("PUT /pins/{pinId} - Update Pin Tests")
    class UpdatePinTests {

        @Test
        @DisplayName("Should update pin successfully")
        void updatePin_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "image.jpg", "image/jpeg", "image content".getBytes());
            MockMultipartFile pinPart = new MockMultipartFile(
                    "pin", "", "application/json", objectMapper.writeValueAsBytes(testPinDTO));

            when(pinService.updatePin(eq(1), any(PinDTO.class), any())).thenReturn(testPinDTO);

            mockMvc.perform(multipart("/pins/1")
                    .file(file)
                    .file(pinPart)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Test Pin"));
        }

        @Test
        @DisplayName("Should fail when pin not found")
        void updatePin_NotFound_Failure() throws Exception {
            MockMultipartFile pinPart = new MockMultipartFile(
                    "pin", "", "application/json", objectMapper.writeValueAsBytes(testPinDTO));

            when(pinService.updatePin(eq(999), any(PinDTO.class), any()))
                    .thenThrow(new InfyPintrestException("Pin not found"));

            mockMvc.perform(multipart("/pins/999")
                    .file(pinPart)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /pins/users/{userId}/pins - Get Pins for User Tests")
    class GetPinsForUserTests {

        @Test
        @DisplayName("Should get pins for user successfully")
        void getPinsForUser_Success() throws Exception {
            when(pinService.getPinsForUser(1, null)).thenReturn(pinList);

            mockMvc.perform(get("/pins/users/1/pins"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Test Pin"));
        }

        @Test
        @DisplayName("Should get pins for user with status filter")
        void getPinsForUser_WithStatus_Success() throws Exception {
            when(pinService.getPinsForUser(1, PinStatus.PUBLISHED)).thenReturn(pinList);

            mockMvc.perform(get("/pins/users/1/pins")
                    .param("status", "PUBLISHED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return empty list when no pins")
        void getPinsForUser_Empty_Success() throws Exception {
            when(pinService.getPinsForUser(1, null)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/pins/users/1/pins"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getPinsForUser_UserNotFound_Failure() throws Exception {
            when(pinService.getPinsForUser(999, null))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/pins/users/999/pins"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /pins/users/{userId}/pins/public - Get Public Pins for User Tests")
    class GetPublicPinsForUserTests {

        @Test
        @DisplayName("Should get public pins successfully")
        void getPublicPins_Success() throws Exception {
            when(pinService.getPublicPinsForUser(1)).thenReturn(pinList);

            mockMvc.perform(get("/pins/users/1/pins/public"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getPublicPins_UserNotFound_Failure() throws Exception {
            when(pinService.getPublicPinsForUser(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/pins/users/999/pins/public"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /pins/boards/{boardId}/pins - Get Pins for Board Tests")
    class GetPinsForBoardTests {

        @Test
        @DisplayName("Should get pins for board successfully")
        void getPinsForBoard_Success() throws Exception {
            when(pinService.getPinsForBoard(1)).thenReturn(pinList);

            mockMvc.perform(get("/pins/boards/1/pins"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should fail when board not found")
        void getPinsForBoard_BoardNotFound_Failure() throws Exception {
            when(pinService.getPinsForBoard(999))
                    .thenThrow(new InfyPintrestException("Board not found"));

            mockMvc.perform(get("/pins/boards/999/pins"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should return empty list when board has no pins")
        void getPinsForBoard_Empty_Success() throws Exception {
            when(pinService.getPinsForBoard(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/pins/boards/1/pins"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /pins/{pinId} - Get Pin Details Tests")
    class GetPinDetailsTests {

        @Test
        @DisplayName("Should get pin details successfully")
        void getPinDetails_Success() throws Exception {
            when(pinService.getPinDetails(1)).thenReturn(testPinViewDTO);

            mockMvc.perform(get("/pins/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Pin"))
                    .andExpect(jsonPath("$.userName").value("testuser"));
        }

        @Test
        @DisplayName("Should fail when pin not found")
        void getPinDetails_NotFound_Failure() throws Exception {
            when(pinService.getPinDetails(999))
                    .thenThrow(new InfyPintrestException("Pin not found"));

            mockMvc.perform(get("/pins/999"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid pin ID")
        void getPinDetails_InvalidId_Failure() throws Exception {
            mockMvc.perform(get("/pins/invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /pins/{pinId}/move - Move Pin Tests")
    class MovePinTests {

        @Test
        @DisplayName("Should move pin successfully")
        void movePin_Success() throws Exception {
            when(pinService.movePin(1, 2)).thenReturn(testPinViewDTO);

            mockMvc.perform(put("/pins/1/move")
                    .param("targetBoardId", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Should fail when pin not found")
        void movePin_PinNotFound_Failure() throws Exception {
            when(pinService.movePin(999, 2))
                    .thenThrow(new InfyPintrestException("Pin not found"));

            mockMvc.perform(put("/pins/999/move")
                    .param("targetBoardId", "2"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when target board not found")
        void movePin_BoardNotFound_Failure() throws Exception {
            when(pinService.movePin(1, 999))
                    .thenThrow(new InfyPintrestException("Target board not found"));

            mockMvc.perform(put("/pins/1/move")
                    .param("targetBoardId", "999"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail without targetBoardId")
        void movePin_MissingTargetBoard_Failure() throws Exception {
            mockMvc.perform(put("/pins/1/move"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /pins/{pinId} - Delete Pin Tests")
    class DeletePinTests {

        @Test
        @DisplayName("Should delete pin successfully")
        void deletePin_Success() throws Exception {
            doNothing().when(pinService).deletePin(1);

            mockMvc.perform(delete("/pins/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Pin deleted successfully"));
        }

        @Test
        @DisplayName("Should delete pin using alternative endpoint")
        void deletePin_AlternativeEndpoint_Success() throws Exception {
            doNothing().when(pinService).deletePin(1);

            mockMvc.perform(delete("/pins/deletepin/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Pin deleted successfully"));
        }

        @Test
        @DisplayName("Should fail when pin not found")
        void deletePin_NotFound_Failure() throws Exception {
            doThrow(new InfyPintrestException("Pin not found"))
                    .when(pinService).deletePin(999);

            mockMvc.perform(delete("/pins/999"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /pins/feed - Get Home Feed Pins Tests")
    class GetHomeFeedPinsTests {

        @Test
        @DisplayName("Should get home feed pins successfully")
        void getHomeFeedPins_Success() throws Exception {
            when(pinService.getHomeFeedPins()).thenReturn(pinList);

            mockMvc.perform(get("/pins/feed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return empty list when no pins")
        void getHomeFeedPins_Empty_Success() throws Exception {
            when(pinService.getHomeFeedPins()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/pins/feed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /pins/sponsored - Get Sponsored Pins Tests")
    class GetSponsoredPinsTests {

        @Test
        @DisplayName("Should get sponsored pins successfully")
        void getSponsoredPins_Success() throws Exception {
            when(pinService.getSponsoredPins()).thenReturn(pinList);

            mockMvc.perform(get("/pins/sponsored"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return empty list when no sponsored pins")
        void getSponsoredPins_Empty_Success() throws Exception {
            when(pinService.getSponsoredPins()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/pins/sponsored"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /pins/category/{category} - Get Pins by Category Tests")
    class GetPinsByCategoryTests {

        @Test
        @DisplayName("Should get pins by category successfully")
        void getPinsByCategory_Success() throws Exception {
            when(pinService.getPinsByCategory("Technology")).thenReturn(pinList);

            mockMvc.perform(get("/pins/category/Technology"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return empty list for category with no pins")
        void getPinsByCategory_Empty_Success() throws Exception {
            when(pinService.getPinsByCategory("EmptyCategory")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/pins/category/EmptyCategory"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail with invalid category")
        void getPinsByCategory_InvalidCategory_Failure() throws Exception {
            when(pinService.getPinsByCategory("InvalidCategory"))
                    .thenThrow(new InfyPintrestException("Invalid category"));

            mockMvc.perform(get("/pins/category/InvalidCategory"))
                    .andExpect(status().isInternalServerError());
        }
    }
}
