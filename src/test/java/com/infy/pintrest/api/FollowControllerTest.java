package com.infy.pintrest.api;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import com.infy.pintrest.dto.UserSummaryDTO;
import com.infy.pintrest.exception.GlobalExceptionHandler;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.FollowUser;

@WebMvcTest(FollowController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowUser followUser;

    private UserSummaryDTO testUser1;
    private UserSummaryDTO testUser2;
    private List<UserSummaryDTO> userList;

    @BeforeEach
    void setUp() {
        testUser1 = new UserSummaryDTO();
        testUser1.setId(1);
        testUser1.setName("testuser1");
        testUser1.setFullName("Test User 1");
        testUser1.setBio("User 1 bio");
        testUser1.setProfilePicUrl("/uploads/profile/user1.jpg");

        testUser2 = new UserSummaryDTO();
        testUser2.setId(2);
        testUser2.setName("testuser2");
        testUser2.setFullName("Test User 2");
        testUser2.setBio("User 2 bio");

        userList = Arrays.asList(testUser1, testUser2);
    }

    @Nested
    @DisplayName("POST /follow/{followerId}/follow/{followingId} - Follow User Tests")
    class FollowUserTests {

        @Test
        @DisplayName("Should follow user successfully")
        void followUser_Success() throws Exception {
            when(followUser.followUser(1, 2)).thenReturn("Successfully followed user");

            mockMvc.perform(post("/follow/1/follow/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Successfully followed user"));
        }

        @Test
        @DisplayName("Should fail when follower not found")
        void followUser_FollowerNotFound_Failure() throws Exception {
            when(followUser.followUser(999, 2))
                    .thenThrow(new InfyPintrestException("Follower user not found"));

            mockMvc.perform(post("/follow/999/follow/2"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when following user not found")
        void followUser_FollowingNotFound_Failure() throws Exception {
            when(followUser.followUser(1, 999))
                    .thenThrow(new InfyPintrestException("User to follow not found"));

            mockMvc.perform(post("/follow/1/follow/999"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when trying to follow self")
        void followUser_FollowSelf_Failure() throws Exception {
            when(followUser.followUser(1, 1))
                    .thenThrow(new InfyPintrestException("Cannot follow yourself"));

            mockMvc.perform(post("/follow/1/follow/1"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when already following")
        void followUser_AlreadyFollowing_Failure() throws Exception {
            when(followUser.followUser(1, 2))
                    .thenThrow(new InfyPintrestException("Already following this user"));

            mockMvc.perform(post("/follow/1/follow/2"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /follow/{followerId}/unfollow/{followingId} - Unfollow User Tests")
    class UnfollowUserTests {

        @Test
        @DisplayName("Should unfollow user successfully")
        void unfollowUser_Success() throws Exception {
            when(followUser.unfollowUser(1, 2)).thenReturn("Successfully unfollowed user");

            mockMvc.perform(delete("/follow/1/unfollow/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Successfully unfollowed user"));
        }

        @Test
        @DisplayName("Should fail when follower not found")
        void unfollowUser_FollowerNotFound_Failure() throws Exception {
            when(followUser.unfollowUser(999, 2))
                    .thenThrow(new InfyPintrestException("Follower user not found"));

            mockMvc.perform(delete("/follow/999/unfollow/2"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when not following")
        void unfollowUser_NotFollowing_Failure() throws Exception {
            when(followUser.unfollowUser(1, 2))
                    .thenThrow(new InfyPintrestException("Not following this user"));

            mockMvc.perform(delete("/follow/1/unfollow/2"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when trying to unfollow self")
        void unfollowUser_UnfollowSelf_Failure() throws Exception {
            when(followUser.unfollowUser(1, 1))
                    .thenThrow(new InfyPintrestException("Cannot unfollow yourself"));

            mockMvc.perform(delete("/follow/1/unfollow/1"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /follow/{userId}/followers - Get Followers Tests")
    class GetFollowersTests {

        @Test
        @DisplayName("Should get followers successfully")
        void getFollowers_Success() throws Exception {
            when(followUser.getFollowers(1)).thenReturn(userList);

            mockMvc.perform(get("/follow/1/followers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("testuser1"))
                    .andExpect(jsonPath("$[1].name").value("testuser2"));
        }

        @Test
        @DisplayName("Should return empty list when no followers")
        void getFollowers_NoFollowers_Success() throws Exception {
            when(followUser.getFollowers(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/follow/1/followers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getFollowers_UserNotFound_Failure() throws Exception {
            when(followUser.getFollowers(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/follow/999/followers"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /follow/{userId}/following - Get Following Tests")
    class GetFollowingTests {

        @Test
        @DisplayName("Should get following successfully")
        void getFollowing_Success() throws Exception {
            when(followUser.getFollowing(1)).thenReturn(userList);

            mockMvc.perform(get("/follow/1/following"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("testuser1"));
        }

        @Test
        @DisplayName("Should return empty list when not following anyone")
        void getFollowing_NotFollowing_Success() throws Exception {
            when(followUser.getFollowing(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/follow/1/following"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getFollowing_UserNotFound_Failure() throws Exception {
            when(followUser.getFollowing(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/follow/999/following"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /follow/{followerId}/isfollowing/{targetId} - Is Following Tests")
    class IsFollowingTests {

        @Test
        @DisplayName("Should return true when following")
        void isFollowing_True_Success() throws Exception {
            when(followUser.isFollowing(1, 2)).thenReturn(true);

            mockMvc.perform(get("/follow/1/isfollowing/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false when not following")
        void isFollowing_False_Success() throws Exception {
            when(followUser.isFollowing(1, 2)).thenReturn(false);

            mockMvc.perform(get("/follow/1/isfollowing/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should fail when follower not found")
        void isFollowing_FollowerNotFound_Failure() throws Exception {
            when(followUser.isFollowing(999, 2))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/follow/999/isfollowing/2"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when target not found")
        void isFollowing_TargetNotFound_Failure() throws Exception {
            when(followUser.isFollowing(1, 999))
                    .thenThrow(new InfyPintrestException("Target user not found"));

            mockMvc.perform(get("/follow/1/isfollowing/999"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /follow/{userId}/followers/count - Get Followers Count Tests")
    class GetFollowersCountTests {

        @Test
        @DisplayName("Should get followers count successfully")
        void getFollowersCount_Success() throws Exception {
            when(followUser.getFollowersCount(1)).thenReturn(100);

            mockMvc.perform(get("/follow/1/followers/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("100"));
        }

        @Test
        @DisplayName("Should return zero when no followers")
        void getFollowersCount_Zero_Success() throws Exception {
            when(followUser.getFollowersCount(1)).thenReturn(0);

            mockMvc.perform(get("/follow/1/followers/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("Should handle large follower count")
        void getFollowersCount_LargeCount_Success() throws Exception {
            when(followUser.getFollowersCount(1)).thenReturn(1000000);

            mockMvc.perform(get("/follow/1/followers/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1000000"));
        }
    }

    @Nested
    @DisplayName("GET /follow/{userId}/following/count - Get Following Count Tests")
    class GetFollowingCountTests {

        @Test
        @DisplayName("Should get following count successfully")
        void getFollowingCount_Success() throws Exception {
            when(followUser.getFollowingCount(1)).thenReturn(50);

            mockMvc.perform(get("/follow/1/following/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("50"));
        }

        @Test
        @DisplayName("Should return zero when not following anyone")
        void getFollowingCount_Zero_Success() throws Exception {
            when(followUser.getFollowingCount(1)).thenReturn(0);

            mockMvc.perform(get("/follow/1/following/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }
    }
}
