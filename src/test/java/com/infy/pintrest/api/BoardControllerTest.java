package com.infy.pintrest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.pintrest.dto.BoardDTO;
import com.infy.pintrest.dto.BoardUpdateDTO;
import com.infy.pintrest.exception.GlobalExceptionHandler;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.BoardService;

@WebMvcTest(BoardController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoardDTO testBoard;
    private BoardUpdateDTO boardUpdateDTO;
    private List<BoardDTO> boardList;

    @BeforeEach
    void setUp() {
        testBoard = new BoardDTO();
        testBoard.setId(1);
        testBoard.setTitle("Test Board");
        testBoard.setDescription("Test Description");
        testBoard.setPrivate(false);
        testBoard.setOwnerId(1);
        testBoard.setOwnerName("Test User");
        testBoard.setCoverImageUrl("/uploads/boards/cover.jpg");
        testBoard.setPinCount(10);

        BoardDTO board2 = new BoardDTO();
        board2.setId(2);
        board2.setTitle("Second Board");
        board2.setDescription("Second Description");
        board2.setPrivate(true);
        board2.setOwnerId(1);

        boardList = Arrays.asList(testBoard, board2);

        boardUpdateDTO = new BoardUpdateDTO();
        boardUpdateDTO.setTitle("Updated Title");
        boardUpdateDTO.setDescription("Updated Description");
        boardUpdateDTO.setIsPrivate(false);
    }

    @Nested
    @DisplayName("POST /board/create_board - Create Board Tests")
    class CreateBoardTests {

        @Test
        @DisplayName("Should create board successfully")
        void createBoard_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "cover.jpg", "image/jpeg", "cover image".getBytes());
            MockMultipartFile boardPart = new MockMultipartFile(
                    "board", "", "application/json", objectMapper.writeValueAsBytes(testBoard));

            when(boardService.createBoard(any(BoardDTO.class), any())).thenReturn(testBoard);

            mockMvc.perform(multipart("/board/create_board")
                    .file(file)
                    .file(boardPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Board"));
        }

        @Test
        @DisplayName("Should fail create board when user not found")
        void createBoard_UserNotFound_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "cover.jpg", "image/jpeg", "cover image".getBytes());
            MockMultipartFile boardPart = new MockMultipartFile(
                    "board", "", "application/json", objectMapper.writeValueAsBytes(testBoard));

            when(boardService.createBoard(any(BoardDTO.class), any()))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(multipart("/board/create_board")
                    .file(file)
                    .file(boardPart))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail create board with duplicate title")
        void createBoard_DuplicateTitle_Failure() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "cover.jpg", "image/jpeg", "cover image".getBytes());
            MockMultipartFile boardPart = new MockMultipartFile(
                    "board", "", "application/json", objectMapper.writeValueAsBytes(testBoard));

            when(boardService.createBoard(any(BoardDTO.class), any()))
                    .thenThrow(new InfyPintrestException("Board with this title already exists"));

            mockMvc.perform(multipart("/board/create_board")
                    .file(file)
                    .file(boardPart))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /board/users/{userId}/boards - Get Boards for User Tests")
    class GetBoardsForUserTests {

        @Test
        @DisplayName("Should get boards for user successfully")
        void getBoardsForUser_Success() throws Exception {
            when(boardService.getBoardForUser(1)).thenReturn(boardList);

            mockMvc.perform(get("/board/users/1/boards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Test Board"))
                    .andExpect(jsonPath("$[1].title").value("Second Board"));
        }

        @Test
        @DisplayName("Should return empty list when user has no boards")
        void getBoardsForUser_NoBoards_Success() throws Exception {
            when(boardService.getBoardForUser(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/board/users/1/boards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getBoardsForUser_UserNotFound_Failure() throws Exception {
            when(boardService.getBoardForUser(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/board/users/999/boards"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid user ID")
        void getBoardsForUser_InvalidId_Failure() throws Exception {
            mockMvc.perform(get("/board/users/invalid/boards"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /board/users/{userId}/boards/public - Get Public Boards Tests")
    class GetPublicBoardsTests {

        @Test
        @DisplayName("Should get public boards successfully")
        void getPublicBoards_Success() throws Exception {
            List<BoardDTO> publicBoards = Arrays.asList(testBoard);
            when(boardService.getPublicBoardsForUser(1)).thenReturn(publicBoards);

            mockMvc.perform(get("/board/users/1/boards/public"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].private").value(false));
        }

        @Test
        @DisplayName("Should return empty list when no public boards")
        void getPublicBoards_NoPublicBoards_Success() throws Exception {
            when(boardService.getPublicBoardsForUser(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/board/users/1/boards/public"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getPublicBoards_UserNotFound_Failure() throws Exception {
            when(boardService.getPublicBoardsForUser(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/board/users/999/boards/public"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /board/boards/{boardId} - Get Board by ID Tests")
    class GetBoardByIdTests {

        @Test
        @DisplayName("Should get board by ID successfully")
        void getBoardById_Success() throws Exception {
            when(boardService.getBoardById(1)).thenReturn(testBoard);

            mockMvc.perform(get("/board/boards/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Board"))
                    .andExpect(jsonPath("$.description").value("Test Description"));
        }

        @Test
        @DisplayName("Should fail when board not found")
        void getBoardById_NotFound_Failure() throws Exception {
            when(boardService.getBoardById(999))
                    .thenThrow(new InfyPintrestException("Board not found"));

            mockMvc.perform(get("/board/boards/999"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid board ID")
        void getBoardById_InvalidId_Failure() throws Exception {
            mockMvc.perform(get("/board/boards/invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /board/boards/{boardId} - Update Board Tests")
    class UpdateBoardTests {

        @Test
        @DisplayName("Should update board successfully")
        void updateBoard_Success() throws Exception {
            BoardDTO updatedBoard = new BoardDTO();
            updatedBoard.setId(1);
            updatedBoard.setTitle("Updated Title");
            updatedBoard.setDescription("Updated Description");

            when(boardService.updateBoard(eq(1), any(BoardUpdateDTO.class))).thenReturn(updatedBoard);

            mockMvc.perform(put("/board/boards/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boardUpdateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"))
                    .andExpect(jsonPath("$.description").value("Updated Description"));
        }

        @Test
        @DisplayName("Should fail when board not found")
        void updateBoard_NotFound_Failure() throws Exception {
            when(boardService.updateBoard(eq(999), any(BoardUpdateDTO.class)))
                    .thenThrow(new InfyPintrestException("Board not found"));

            mockMvc.perform(put("/board/boards/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boardUpdateDTO)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with duplicate title")
        void updateBoard_DuplicateTitle_Failure() throws Exception {
            when(boardService.updateBoard(eq(1), any(BoardUpdateDTO.class)))
                    .thenThrow(new InfyPintrestException("Board with this title already exists"));

            mockMvc.perform(put("/board/boards/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boardUpdateDTO)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when user not authorized")
        void updateBoard_Unauthorized_Failure() throws Exception {
            when(boardService.updateBoard(eq(1), any(BoardUpdateDTO.class)))
                    .thenThrow(new InfyPintrestException("Not authorized to update this board"));

            mockMvc.perform(put("/board/boards/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boardUpdateDTO)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /board/boards/{boardId} - Delete Board Tests")
    class DeleteBoardTests {

        @Test
        @DisplayName("Should delete board successfully")
        void deleteBoard_Success() throws Exception {
            doNothing().when(boardService).deleteBoard(1);

            mockMvc.perform(delete("/board/boards/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Board deleted successfully"));
        }

        @Test
        @DisplayName("Should fail when board not found")
        void deleteBoard_NotFound_Failure() throws Exception {
            doThrow(new InfyPintrestException("Board not found"))
                    .when(boardService).deleteBoard(999);

            mockMvc.perform(delete("/board/boards/999"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when user not authorized")
        void deleteBoard_Unauthorized_Failure() throws Exception {
            doThrow(new InfyPintrestException("Not authorized to delete this board"))
                    .when(boardService).deleteBoard(1);

            mockMvc.perform(delete("/board/boards/1"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid board ID")
        void deleteBoard_InvalidId_Failure() throws Exception {
            mockMvc.perform(delete("/board/boards/invalid"))
                    .andExpect(status().isBadRequest());
        }
    }
}
