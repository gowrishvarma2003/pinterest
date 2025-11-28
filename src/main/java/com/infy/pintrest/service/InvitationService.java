package com.infy.pintrest.service;

import java.util.List;

import com.infy.pintrest.dto.BoardDTO;
import com.infy.pintrest.dto.InvitationCreateDTO;
import com.infy.pintrest.dto.InvitationDTO;
import com.infy.pintrest.dto.UserSummaryDTO;
import com.infy.pintrest.exception.InfyPintrestException;

public interface InvitationService {
    InvitationDTO sendInvitation(Integer senderId, InvitationCreateDTO createDTO) throws InfyPintrestException;
    InvitationDTO sendInvitationByEmail(Integer senderId, Integer boardId, String receiverEmail) throws InfyPintrestException;
    List<InvitationDTO> getReceivedInvitations(Integer userId);
    List<InvitationDTO> getSentInvitations(Integer userId);
    List<InvitationDTO> getSentInvitationsForBoard(Integer userId, Integer boardId);
    InvitationDTO respondInvitation(Integer invitationId, Integer receiverId, boolean accept) throws InfyPintrestException;
    void cancelInvitation(Integer invitationId, Integer senderId) throws InfyPintrestException;
    List<UserSummaryDTO> getBoardCollaborators(Integer boardId) throws InfyPintrestException;
    List<BoardDTO> getCollabBoards(Integer userId) throws InfyPintrestException;
    void removeCollaborator(Integer boardId, Integer collaboratorId, Integer requesterId) throws InfyPintrestException;
}
