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

import com.infy.pintrest.entity.Board;
import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.repository.BoardRepository;
import com.infy.pintrest.repository.PinRepository;
import com.infy.pintrest.repository.UserRepository;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PinRepository pinRepository;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private UserRepository userRepository;

    private Pin testPin;
    private Board testBoard;
    private User testUser;
    private List<Pin> pinList;
    private List<Board> boardList;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("testuser");
        testUser.setUsername("testuser");
        testUser.setProfilePath("/uploads/profile/user.jpg");

        testBoard = new Board();
        testBoard.setId(1);
        testBoard.setTitle("Test Board");
        testBoard.setDescription("Test Board Description");
        testBoard.setCoverImageUrl("/uploads/boards/cover.jpg");
        testBoard.setOwner(testUser);
        testBoard.setPins(Collections.emptyList());

        testPin = new Pin();
        testPin.setId(1);
        testPin.setTitle("Test Pin");
        testPin.setDescription("Test Pin Description");
        testPin.setImageUrl("/uploads/pins/image.jpg");
        testPin.setBoard(testBoard);
        testPin.setUser(testUser);

        pinList = Arrays.asList(testPin);
        boardList = Arrays.asList(testBoard);
        userList = Arrays.asList(testUser);
    }

    @Nested
    @DisplayName("GET /search - General Search Tests")
    class GeneralSearchTests {

        @Test
        @DisplayName("Should search successfully with results")
        void search_WithResults_Success() throws Exception {
            when(pinRepository.searchPublicPins("test")).thenReturn(pinList);
            when(boardRepository.searchPublicBoards("test")).thenReturn(boardList);
            when(userRepository.searchUsers("test")).thenReturn(userList);

            mockMvc.perform(get("/search")
                    .param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pins").isArray())
                    .andExpect(jsonPath("$.boards").isArray())
                    .andExpect(jsonPath("$.users").isArray())
                    .andExpect(jsonPath("$.pins.length()").value(1))
                    .andExpect(jsonPath("$.boards.length()").value(1))
                    .andExpect(jsonPath("$.users.length()").value(1));
        }

        @Test
        @DisplayName("Should return empty results for empty query")
        void search_EmptyQuery_Success() throws Exception {
            mockMvc.perform(get("/search")
                    .param("q", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pins").isArray())
                    .andExpect(jsonPath("$.pins.length()").value(0))
                    .andExpect(jsonPath("$.boards.length()").value(0))
                    .andExpect(jsonPath("$.users.length()").value(0));
        }

        @Test
        @DisplayName("Should return empty results for whitespace query")
        void search_WhitespaceQuery_Success() throws Exception {
            mockMvc.perform(get("/search")
                    .param("q", "   "))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pins.length()").value(0));
        }

        @Test
        @DisplayName("Should return empty results when no matches")
        void search_NoMatches_Success() throws Exception {
            when(pinRepository.searchPublicPins("nonexistent")).thenReturn(Collections.emptyList());
            when(boardRepository.searchPublicBoards("nonexistent")).thenReturn(Collections.emptyList());
            when(userRepository.searchUsers("nonexistent")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/search")
                    .param("q", "nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pins.length()").value(0))
                    .andExpect(jsonPath("$.boards.length()").value(0))
                    .andExpect(jsonPath("$.users.length()").value(0));
        }

        @Test
        @DisplayName("Should fail without query parameter")
        void search_MissingQuery_Failure() throws Exception {
            // Spring MVC returns 400 Bad Request when required @RequestParam is missing
            mockMvc.perform(get("/search"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /search/pins - Search Pins Tests")
    class SearchPinsTests {

        @Test
        @DisplayName("Should search pins successfully")
        void searchPins_Success() throws Exception {
            when(pinRepository.searchPublicPins("test")).thenReturn(pinList);

            mockMvc.perform(get("/search/pins")
                    .param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Pin"));
        }

        @Test
        @DisplayName("Should return empty list for empty query")
        void searchPins_EmptyQuery_Success() throws Exception {
            mockMvc.perform(get("/search/pins")
                    .param("q", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return empty list when no pins match")
        void searchPins_NoMatch_Success() throws Exception {
            when(pinRepository.searchPublicPins("nonexistent")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/search/pins")
                    .param("q", "nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail without query parameter")
        void searchPins_MissingQuery_Failure() throws Exception {
            // Spring MVC returns 400 Bad Request when required @RequestParam is missing
            mockMvc.perform(get("/search/pins"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle special characters in query")
        void searchPins_SpecialCharacters_Success() throws Exception {
            when(pinRepository.searchPublicPins("test@#$")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/search/pins")
                    .param("q", "test@#$"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /search/boards - Search Boards Tests")
    class SearchBoardsTests {

        @Test
        @DisplayName("Should search boards successfully")
        void searchBoards_Success() throws Exception {
            when(boardRepository.searchPublicBoards("test")).thenReturn(boardList);

            mockMvc.perform(get("/search/boards")
                    .param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Board"));
        }

        @Test
        @DisplayName("Should return empty list for empty query")
        void searchBoards_EmptyQuery_Success() throws Exception {
            mockMvc.perform(get("/search/boards")
                    .param("q", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return empty list when no boards match")
        void searchBoards_NoMatch_Success() throws Exception {
            when(boardRepository.searchPublicBoards("nonexistent")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/search/boards")
                    .param("q", "nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail without query parameter")
        void searchBoards_MissingQuery_Failure() throws Exception {
            // Spring MVC returns 400 Bad Request when required @RequestParam is missing
            mockMvc.perform(get("/search/boards"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should include board owner info")
        void searchBoards_IncludesOwnerInfo_Success() throws Exception {
            when(boardRepository.searchPublicBoards("test")).thenReturn(boardList);

            mockMvc.perform(get("/search/boards")
                    .param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].ownerId").value(1))
                    .andExpect(jsonPath("$[0].ownerName").value("testuser"));
        }
    }

    @Nested
    @DisplayName("GET /search/users - Search Users Tests")
    class SearchUsersTests {

        @Test
        @DisplayName("Should search users successfully")
        void searchUsers_Success() throws Exception {
            when(userRepository.searchUsers("test")).thenReturn(userList);

            mockMvc.perform(get("/search/users")
                    .param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("testuser"));
        }

        @Test
        @DisplayName("Should return empty list for empty query")
        void searchUsers_EmptyQuery_Success() throws Exception {
            mockMvc.perform(get("/search/users")
                    .param("q", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return empty list when no users match")
        void searchUsers_NoMatch_Success() throws Exception {
            when(userRepository.searchUsers("nonexistent")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/search/users")
                    .param("q", "nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail without query parameter")
        void searchUsers_MissingQuery_Failure() throws Exception {
            // Spring MVC returns 400 Bad Request when required @RequestParam is missing
            mockMvc.perform(get("/search/users"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should include user profile info")
        void searchUsers_IncludesProfileInfo_Success() throws Exception {
            when(userRepository.searchUsers("test")).thenReturn(userList);

            mockMvc.perform(get("/search/users")
                    .param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].username").value("testuser"));
        }
    }

    @Nested
    @DisplayName("Invalid Request Tests")
    class InvalidRequestTests {

        @Test
        @DisplayName("Should fail with POST method on search")
        void search_PostMethod_Failure() throws Exception {
            mockMvc.perform(post("/search")
                    .param("q", "test"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with PUT method on search")
        void search_PutMethod_Failure() throws Exception {
            mockMvc.perform(put("/search")
                    .param("q", "test"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with DELETE method on search")
        void search_DeleteMethod_Failure() throws Exception {
            mockMvc.perform(delete("/search")
                    .param("q", "test"))
                    .andExpect(status().isMethodNotAllowed());
        }
    }
}
