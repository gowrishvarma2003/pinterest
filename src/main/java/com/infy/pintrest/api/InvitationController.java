package com.infy.pintrest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.infy.pintrest.dto.InvitationCreateDTO;
import com.infy.pintrest.dto.InvitationDTO;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.InvitationService;

import java.util.List;

@RestController
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

    @GetMapping("/{userId}/received")
    public ResponseEntity<List<InvitationDTO>> receivedInvitations(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(invitationService.getReceivedInvitations(userId));
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
}
