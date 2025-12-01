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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.pinterest.enums.InvitationStatus;
import com.infy.pintrest.dto.BoardDTO;
import com.infy.pintrest.dto.InvitationCreateDTO;
import com.infy.pintrest.dto.InvitationDTO;
import com.infy.pintrest.dto.UserSummaryDTO;
import com.infy.pintrest.exception.GlobalExceptionHandler;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.InvitationService;

@WebMvcTest(InvitationController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
public class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvitationService invitationService;

    @Autowired
    private ObjectMapper objectMapper;

    private InvitationDTO testInvitation;
    private InvitationCreateDTO createDTO;
    private UserSummaryDTO testUser;
    private BoardDTO testBoard;
    private List<InvitationDTO> invitationList;

    @BeforeEach
    void setUp() {
        testInvitation = new InvitationDTO();
        testInvitation.setId(1);
        testInvitation.setSenderId(1);
        testInvitation.setReceiverId(2);
        testInvitation.setBoardId(1);
        testInvitation.setStatus(InvitationStatus.PENDING);
        testInvitation.setSenderName("sender");
        testInvitation.setReceiverName("receiver");
        testInvitation.setBoardTitle("Test Board");

        createDTO = new InvitationCreateDTO();
        createDTO.setReceiverId(2);
        createDTO.setBoardId(1);

        testUser = new UserSummaryDTO();
        testUser.setId(1);
        testUser.setName("testuser");
        testUser.setFullName("Test User");

        testBoard = new BoardDTO();
        testBoard.setId(1);
        testBoard.setTitle("Collab Board");
        testBoard.setOwnerId(1);

        invitationList = Arrays.asList(testInvitation);
    }

    @Nested
    @DisplayName("POST /api/invitations/{senderId}/send - Send Invitation Tests")
    class SendInvitationTests {

        @Test
        @DisplayName("Should send invitation successfully")
        void sendInvitation_Success() throws Exception {
            when(invitationService.sendInvitation(eq(1), any(InvitationCreateDTO.class)))
                    .thenReturn(testInvitation);

            mockMvc.perform(post("/api/invitations/1/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.senderId").value(1))
                    .andExpect(jsonPath("$.receiverId").value(2));
        }

        @Test
        @DisplayName("Should fail when sender not found")
        void sendInvitation_SenderNotFound_Failure() throws Exception {
            when(invitationService.sendInvitation(eq(999), any(InvitationCreateDTO.class)))
                    .thenThrow(new InfyPintrestException("Sender not found"));

            mockMvc.perform(post("/api/invitations/999/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when receiver not found")
        void sendInvitation_ReceiverNotFound_Failure() throws Exception {
            when(invitationService.sendInvitation(eq(1), any(InvitationCreateDTO.class)))
                    .thenThrow(new InfyPintrestException("Receiver not found"));

            mockMvc.perform(post("/api/invitations/1/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when board not found")
        void sendInvitation_BoardNotFound_Failure() throws Exception {
            when(invitationService.sendInvitation(eq(1), any(InvitationCreateDTO.class)))
                    .thenThrow(new InfyPintrestException("Board not found"));

            mockMvc.perform(post("/api/invitations/1/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when sender not board owner")
        void sendInvitation_NotBoardOwner_Failure() throws Exception {
            when(invitationService.sendInvitation(eq(1), any(InvitationCreateDTO.class)))
                    .thenThrow(new InfyPintrestException("Only board owner can send invitations"));

            mockMvc.perform(post("/api/invitations/1/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /api/invitations/{senderId}/send-by-email - Send Invitation by Email Tests")
    class SendInvitationByEmailTests {

        @Test
        @DisplayName("Should send invitation by email successfully")
        void sendInvitationByEmail_Success() throws Exception {
            when(invitationService.sendInvitationByEmail(eq(1), eq(1), eq("test@example.com")))
                    .thenReturn(testInvitation);

            String requestBody = "{\"boardId\": 1, \"email\": \"test@example.com\"}";

            mockMvc.perform(post("/api/invitations/1/send-by-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Should fail when email not found")
        void sendInvitationByEmail_EmailNotFound_Failure() throws Exception {
            when(invitationService.sendInvitationByEmail(eq(1), eq(1), eq("notfound@example.com")))
                    .thenThrow(new InfyPintrestException("User with this email not found"));

            String requestBody = "{\"boardId\": 1, \"email\": \"notfound@example.com\"}";

            mockMvc.perform(post("/api/invitations/1/send-by-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail with invalid email format")
        void sendInvitationByEmail_InvalidEmail_Failure() throws Exception {
            when(invitationService.sendInvitationByEmail(eq(1), eq(1), eq("invalid-email")))
                    .thenThrow(new InfyPintrestException("Invalid email format"));

            String requestBody = "{\"boardId\": 1, \"email\": \"invalid-email\"}";

            mockMvc.perform(post("/api/invitations/1/send-by-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/invitations/{userId}/received - Get Received Invitations Tests")
    class GetReceivedInvitationsTests {

        @Test
        @DisplayName("Should get received invitations successfully")
        void getReceivedInvitations_Success() throws Exception {
            when(invitationService.getReceivedInvitations(1)).thenReturn(invitationList);

            mockMvc.perform(get("/api/invitations/1/received"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].receiverId").value(2));
        }

        @Test
        @DisplayName("Should return empty list when no invitations")
        void getReceivedInvitations_Empty_Success() throws Exception {
            when(invitationService.getReceivedInvitations(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/invitations/1/received"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/invitations/{userId}/sent - Get Sent Invitations Tests")
    class GetSentInvitationsTests {

        @Test
        @DisplayName("Should get sent invitations successfully")
        void getSentInvitations_Success() throws Exception {
            when(invitationService.getSentInvitations(1)).thenReturn(invitationList);

            mockMvc.perform(get("/api/invitations/1/sent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].senderId").value(1));
        }

        @Test
        @DisplayName("Should return empty list when no sent invitations")
        void getSentInvitations_Empty_Success() throws Exception {
            when(invitationService.getSentInvitations(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/invitations/1/sent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/invitations/{userId}/sent/board/{boardId} - Get Sent Invitations for Board Tests")
    class GetSentInvitationsForBoardTests {

        @Test
        @DisplayName("Should get sent invitations for board successfully")
        void getSentInvitationsForBoard_Success() throws Exception {
            when(invitationService.getSentInvitationsForBoard(1, 1)).thenReturn(invitationList);

            mockMvc.perform(get("/api/invitations/1/sent/board/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].boardId").value(1));
        }

        @Test
        @DisplayName("Should return empty list for board with no invitations")
        void getSentInvitationsForBoard_Empty_Success() throws Exception {
            when(invitationService.getSentInvitationsForBoard(1, 1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/invitations/1/sent/board/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("PUT /api/invitations/{invitationId}/respond - Respond to Invitation Tests")
    class RespondInvitationTests {

        @Test
        @DisplayName("Should accept invitation successfully")
        void respondInvitation_Accept_Success() throws Exception {
            InvitationDTO acceptedInvitation = new InvitationDTO();
            acceptedInvitation.setId(1);
            acceptedInvitation.setStatus(InvitationStatus.ACCEPTED);

            when(invitationService.respondInvitation(1, 2, true)).thenReturn(acceptedInvitation);

            mockMvc.perform(put("/api/invitations/1/respond")
                    .param("receiverId", "2")
                    .param("accept", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ACCEPTED"));
        }

        @Test
        @DisplayName("Should decline invitation successfully")
        void respondInvitation_Decline_Success() throws Exception {
            InvitationDTO declinedInvitation = new InvitationDTO();
            declinedInvitation.setId(1);
            declinedInvitation.setStatus(InvitationStatus.DECLINED);

            when(invitationService.respondInvitation(1, 2, false)).thenReturn(declinedInvitation);

            mockMvc.perform(put("/api/invitations/1/respond")
                    .param("receiverId", "2")
                    .param("accept", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("DECLINED"));
        }

        @Test
        @DisplayName("Should fail when invitation not found")
        void respondInvitation_NotFound_Failure() throws Exception {
            when(invitationService.respondInvitation(999, 2, true))
                    .thenThrow(new InfyPintrestException("Invitation not found"));

            mockMvc.perform(put("/api/invitations/999/respond")
                    .param("receiverId", "2")
                    .param("accept", "true"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when not the receiver")
        void respondInvitation_NotReceiver_Failure() throws Exception {
            when(invitationService.respondInvitation(1, 3, true))
                    .thenThrow(new InfyPintrestException("Not authorized to respond to this invitation"));

            mockMvc.perform(put("/api/invitations/1/respond")
                    .param("receiverId", "3")
                    .param("accept", "true"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /api/invitations/{invitationId}/cancel - Cancel Invitation Tests")
    class CancelInvitationTests {

        @Test
        @DisplayName("Should cancel invitation successfully")
        void cancelInvitation_Success() throws Exception {
            doNothing().when(invitationService).cancelInvitation(1, 1);

            mockMvc.perform(delete("/api/invitations/1/cancel")
                    .param("senderId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Invitation cancelled"));
        }

        @Test
        @DisplayName("Should fail when invitation not found")
        void cancelInvitation_NotFound_Failure() throws Exception {
            doThrow(new InfyPintrestException("Invitation not found"))
                    .when(invitationService).cancelInvitation(999, 1);

            mockMvc.perform(delete("/api/invitations/999/cancel")
                    .param("senderId", "1"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when not the sender")
        void cancelInvitation_NotSender_Failure() throws Exception {
            doThrow(new InfyPintrestException("Not authorized to cancel this invitation"))
                    .when(invitationService).cancelInvitation(1, 2);

            mockMvc.perform(delete("/api/invitations/1/cancel")
                    .param("senderId", "2"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/invitations/boards/{boardId}/collaborators - Get Board Collaborators Tests")
    class GetBoardCollaboratorsTests {

        @Test
        @DisplayName("Should get board collaborators successfully")
        void getBoardCollaborators_Success() throws Exception {
            List<UserSummaryDTO> collaborators = Arrays.asList(testUser);
            when(invitationService.getBoardCollaborators(1)).thenReturn(collaborators);

            mockMvc.perform(get("/api/invitations/boards/1/collaborators"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("testuser"));
        }

        @Test
        @DisplayName("Should return empty list when no collaborators")
        void getBoardCollaborators_Empty_Success() throws Exception {
            when(invitationService.getBoardCollaborators(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/invitations/boards/1/collaborators"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when board not found")
        void getBoardCollaborators_BoardNotFound_Failure() throws Exception {
            when(invitationService.getBoardCollaborators(999))
                    .thenThrow(new InfyPintrestException("Board not found"));

            mockMvc.perform(get("/api/invitations/boards/999/collaborators"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/invitations/users/{userId}/collab-boards - Get Collab Boards Tests")
    class GetCollabBoardsTests {

        @Test
        @DisplayName("Should get collab boards successfully")
        void getCollabBoards_Success() throws Exception {
            List<BoardDTO> boards = Arrays.asList(testBoard);
            when(invitationService.getCollabBoards(1)).thenReturn(boards);

            mockMvc.perform(get("/api/invitations/users/1/collab-boards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Collab Board"));
        }

        @Test
        @DisplayName("Should return empty list when no collab boards")
        void getCollabBoards_Empty_Success() throws Exception {
            when(invitationService.getCollabBoards(1)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/invitations/users/1/collab-boards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getCollabBoards_UserNotFound_Failure() throws Exception {
            when(invitationService.getCollabBoards(999))
                    .thenThrow(new InfyPintrestException("User not found"));

            mockMvc.perform(get("/api/invitations/users/999/collab-boards"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /api/invitations/boards/{boardId}/collaborators/{collaboratorId} - Remove Collaborator Tests")
    class RemoveCollaboratorTests {

        @Test
        @DisplayName("Should remove collaborator successfully")
        void removeCollaborator_Success() throws Exception {
            doNothing().when(invitationService).removeCollaborator(1, 2, 1);

            mockMvc.perform(delete("/api/invitations/boards/1/collaborators/2")
                    .param("requesterId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Collaborator removed"));
        }

        @Test
        @DisplayName("Should fail when board not found")
        void removeCollaborator_BoardNotFound_Failure() throws Exception {
            doThrow(new InfyPintrestException("Board not found"))
                    .when(invitationService).removeCollaborator(999, 2, 1);

            mockMvc.perform(delete("/api/invitations/boards/999/collaborators/2")
                    .param("requesterId", "1"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when collaborator not found")
        void removeCollaborator_CollaboratorNotFound_Failure() throws Exception {
            doThrow(new InfyPintrestException("Collaborator not found"))
                    .when(invitationService).removeCollaborator(1, 999, 1);

            mockMvc.perform(delete("/api/invitations/boards/1/collaborators/999")
                    .param("requesterId", "1"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should fail when not authorized")
        void removeCollaborator_NotAuthorized_Failure() throws Exception {
            doThrow(new InfyPintrestException("Not authorized to remove collaborators"))
                    .when(invitationService).removeCollaborator(1, 2, 3);

            mockMvc.perform(delete("/api/invitations/boards/1/collaborators/2")
                    .param("requesterId", "3"))
                    .andExpect(status().isInternalServerError());
        }
    }
}
