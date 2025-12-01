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
import com.infy.pintrest.service.LikePinServiceImpl;

@WebMvcTest(LikeController.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikePinServiceImpl likeService;

    private Pin testPin;
    private List<Pin> likedPins;

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

        likedPins = Arrays.asList(testPin, pin2);
    }

    @Nested
    @DisplayName("POST /api/pins/like - Like Pin Tests")
    class LikePinTests {

        @Test
        @DisplayName("Should like pin successfully")
        void likePin_Success() throws Exception {
            when(likeService.likePin(1, 1)).thenReturn("Pin liked successfully");

            mockMvc.perform(post("/api/pins/like")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Pin liked successfully"));
        }

        @Test
        @DisplayName("Should return message when already liked")
        void likePin_AlreadyLiked_Success() throws Exception {
            when(likeService.likePin(1, 1)).thenReturn("You already liked this pin");

            mockMvc.perform(post("/api/pins/like")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("You already liked this pin"));
        }


    }

    @Nested
    @DisplayName("GET /api/pins/{pinId}/likes - Get Likes Count Tests")
    class GetLikesTests {

        @Test
        @DisplayName("Should get likes count successfully")
        void getLikes_Success() throws Exception {
            when(likeService.getLikes(1)).thenReturn(100);

            mockMvc.perform(get("/api/pins/1/likes"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("100"));
        }

        @Test
        @DisplayName("Should return zero when no likes")
        void getLikes_Zero_Success() throws Exception {
            when(likeService.getLikes(1)).thenReturn(0);

            mockMvc.perform(get("/api/pins/1/likes"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("Should handle large like count")
        void getLikes_LargeCount_Success() throws Exception {
            when(likeService.getLikes(1)).thenReturn(1000000);

            mockMvc.perform(get("/api/pins/1/likes"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1000000"));
        }
    }

    @Nested
    @DisplayName("GET /api/pins/isLiked - Is Pin Liked Tests")
    class IsLikedTests {

        @Test
        @DisplayName("Should return true when liked")
        void isLiked_True_Success() throws Exception {
            when(likeService.isLiked(1, 1)).thenReturn(true);

            mockMvc.perform(get("/api/pins/isLiked")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false when not liked")
        void isLiked_False_Success() throws Exception {
            when(likeService.isLiked(1, 1)).thenReturn(false);

            mockMvc.perform(get("/api/pins/isLiked")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

    }

    @Nested
    @DisplayName("GET /api/pins/liked - Get Liked Pins Tests")
    class GetLikedPinsTests {

        @Test
        @DisplayName("Should get liked pins successfully")
        void getLikedPins_Success() throws Exception {
            when(likeService.getLikedPins(1)).thenReturn(likedPins);

            mockMvc.perform(get("/api/pins/liked")
                    .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Pin"));
        }

        @Test
        @DisplayName("Should return empty list when no liked pins")
        void getLikedPins_Empty_Success() throws Exception {
            when(likeService.getLikedPins(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/pins/liked")
                    .param("userId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("DELETE /api/pins/unlike - Unlike Pin Tests")
    class UnlikePinTests {

        @Test
        @DisplayName("Should unlike pin successfully")
        void unlikePin_Success() throws Exception {
            when(likeService.unlikePin(1, 1)).thenReturn("Pin unliked successfully");

            mockMvc.perform(delete("/api/pins/unlike")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Pin unliked successfully"));
        }

        @Test
        @DisplayName("Should return message when not liked")
        void unlikePin_NotLiked_Success() throws Exception {
            when(likeService.unlikePin(1, 1)).thenReturn("You haven't liked this pin");

            mockMvc.perform(delete("/api/pins/unlike")
                    .param("userId", "1")
                    .param("pinId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("You haven't liked this pin"));
        }



    }
}
