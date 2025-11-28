package com.infy.pintrest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.infy.pintrest.dto.BoardDTO;
import com.infy.pintrest.dto.InvitationCreateDTO;
import com.infy.pintrest.dto.InvitationDTO;
import com.infy.pintrest.dto.UserSummaryDTO;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.InvitationService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/{senderId}/send")
    public ResponseEntity<InvitationDTO> sendInvitation(
            @PathVariable Integer senderId,
            @RequestBody InvitationCreateDTO dto) throws InfyPintrestException {
        return ResponseEntity.ok(invitationService.sendInvitation(senderId, dto));
    }

    @PostMapping("/{senderId}/send-by-email")
    public ResponseEntity<InvitationDTO> sendInvitationByEmail(
            @PathVariable Integer senderId,
            @RequestBody Map<String, Object> request) throws InfyPintrestException {
        Integer boardId = (Integer) request.get("boardId");
        String email = (String) request.get("email");
        return ResponseEntity.ok(invitationService.sendInvitationByEmail(senderId, boardId, email));
    }

    @GetMapping("/{userId}/received")
    public ResponseEntity<List<InvitationDTO>> receivedInvitations(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(invitationService.getReceivedInvitations(userId));
    }

    @GetMapping("/{userId}/sent")
    public ResponseEntity<List<InvitationDTO>> sentInvitations(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(invitationService.getSentInvitations(userId));
    }

    @GetMapping("/{userId}/sent/board/{boardId}")
    public ResponseEntity<List<InvitationDTO>> sentInvitationsForBoard(
            @PathVariable Integer userId,
            @PathVariable Integer boardId) {
        return ResponseEntity.ok(invitationService.getSentInvitationsForBoard(userId, boardId));
    }

    @PutMapping("/{invitationId}/respond")
    public ResponseEntity<InvitationDTO> respondInvitation(
            @PathVariable Integer invitationId,
            @RequestParam Integer receiverId,
            @RequestParam boolean accept) throws InfyPintrestException {
        return ResponseEntity.ok(
                invitationService.respondInvitation(invitationId, receiverId, accept));
    }

    @DeleteMapping("/{invitationId}/cancel")
    public ResponseEntity<String> cancelInvitation(
            @PathVariable Integer invitationId,
            @RequestParam Integer senderId) throws InfyPintrestException {
        invitationService.cancelInvitation(invitationId, senderId);
        return ResponseEntity.ok("Invitation cancelled");
    }

    @GetMapping("/boards/{boardId}/collaborators")
    public ResponseEntity<List<UserSummaryDTO>> getBoardCollaborators(
            @PathVariable Integer boardId) throws InfyPintrestException {
        return ResponseEntity.ok(invitationService.getBoardCollaborators(boardId));
    }

    @GetMapping("/users/{userId}/collab-boards")
    public ResponseEntity<List<BoardDTO>> getCollabBoards(
            @PathVariable Integer userId) throws InfyPintrestException {
        return ResponseEntity.ok(invitationService.getCollabBoards(userId));
    }

    @DeleteMapping("/boards/{boardId}/collaborators/{collaboratorId}")
    public ResponseEntity<String> removeCollaborator(
            @PathVariable Integer boardId,
            @PathVariable Integer collaboratorId,
            @RequestParam Integer requesterId) throws InfyPintrestException {
        invitationService.removeCollaborator(boardId, collaboratorId, requesterId);
        return ResponseEntity.ok("Collaborator removed");
    }
}
