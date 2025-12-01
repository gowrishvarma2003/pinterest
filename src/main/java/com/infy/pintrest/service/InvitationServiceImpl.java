package com.infy.pintrest.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.pinterest.enums.InvitationStatus;
import com.infy.pintrest.dto.InvitationCreateDTO;
import com.infy.pintrest.dto.InvitationDTO;
import com.infy.pintrest.entity.Board;
import com.infy.pintrest.entity.Invitation;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.BoardRepository;
import com.infy.pintrest.repository.InvitationRepository;
import com.infy.pintrest.repository.UserRepository;

@Service
@Transactional
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private InvitationDTO mapToDTO(Invitation inv) {
        InvitationDTO dto = new InvitationDTO();
        dto.setId(inv.getId());
        dto.setSenderId(inv.getSender().getId());
        dto.setReceiverId(inv.getReceiver().getId());
        dto.setStatus(inv.getStatus());
        
        // Sender info
        User sender = inv.getSender();
        dto.setSenderName(sender.getName());
        dto.setSenderFullName(sender.getUsername() != null ? sender.getUsername() : sender.getName());
        dto.setSenderEmail(sender.getEmail());
        dto.setSenderAvatar(sender.getProfilePath());
        
        // Receiver info
        User receiver = inv.getReceiver();
        dto.setReceiverName(receiver.getName());
        dto.setReceiverEmail(receiver.getEmail());
        dto.setReceiverAvatar(receiver.getProfilePath());
        
        // Board info
        if (inv.getBoard() != null) {
            dto.setBoardId(inv.getBoard().getId());
            dto.setBoardTitle(inv.getBoard().getTitle());
            dto.setBoardCoverUrl(inv.getBoard().getCoverImageUrl());
        }
        
        // Format sent time
        dto.setSentAt(formatTimeAgo(inv.getSentAt()));
        
        return dto;
    }
    
    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Just now";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        long weeks = days / 7;
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";
        return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
    }

    @Override
    public InvitationDTO sendInvitation(Integer senderId, InvitationCreateDTO createDTO) throws InfyPintrestException {
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) {
            throw new InfyPintrestException("Service.USER_NOT_FOUND");
        }
        User sender = senderOpt.get();

        Optional<User> receiverOpt = userRepository.findById(createDTO.getReceiverId());
        if (receiverOpt.isEmpty()) {
            throw new InfyPintrestException("Service.USER_NOT_FOUND");
        }
        User receiver = receiverOpt.get();

        Board board = null;
        if (createDTO.getBoardId() != null) {
            Optional<Board> boardOpt = boardRepository.findById(createDTO.getBoardId());
            if (boardOpt.isEmpty()) {
                throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
            }
            board = boardOpt.get();
            
            // Check if invitation already exists
            Optional<Invitation> existing = invitationRepository.findBySenderIdAndReceiverIdAndBoardId(
                senderId, createDTO.getReceiverId(), createDTO.getBoardId());
            if (existing.isPresent()) {
                throw new InfyPintrestException("Service.INVITATION_ALREADY_EXISTS");
            }
        }

        Invitation inv = new Invitation();
        inv.setSender(sender);
        inv.setReceiver(receiver);
        inv.setBoard(board);
        inv.setStatus(InvitationStatus.PENDING);
        inv.setSentAt(LocalDateTime.now());

        Invitation saved = invitationRepository.save(inv);
        return mapToDTO(saved);
    }

    @Override
    public InvitationDTO sendInvitationByEmail(Integer senderId, Integer boardId, String receiverEmail) throws InfyPintrestException {
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) {
            throw new InfyPintrestException("Service.USER_NOT_FOUND");
        }
        User sender = senderOpt.get();

        Optional<User> receiverOpt = userRepository.findByEmail(receiverEmail);
        if (receiverOpt.isEmpty()) {
            throw new InfyPintrestException("Service.USER_NOT_FOUND_WITH_EMAIL");
        }
        User receiver = receiverOpt.get();
        
        if (sender.getId().equals(receiver.getId())) {
            throw new InfyPintrestException("Service.CANNOT_INVITE_SELF");
        }

        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board board = boardOpt.get();
        
        // Check if user is already a collaborator
        if (board.getCollaborators() != null && board.getCollaborators().stream()
                .anyMatch(c -> c.getId().equals(receiver.getId()))) {
            throw new InfyPintrestException("Service.ALREADY_COLLABORATOR");
        }
        
        // Check if invitation already exists
        Optional<Invitation> existing = invitationRepository.findBySenderIdAndReceiverIdAndBoardId(
            senderId, receiver.getId(), boardId);
        if (existing.isPresent() && existing.get().getStatus() == InvitationStatus.PENDING) {
            throw new InfyPintrestException("Service.INVITATION_ALREADY_EXISTS");
        }

        Invitation inv = new Invitation();
        inv.setSender(sender);
        inv.setReceiver(receiver);
        inv.setBoard(board);
        inv.setStatus(InvitationStatus.PENDING);
        inv.setSentAt(LocalDateTime.now());

        Invitation saved = invitationRepository.save(inv);
        return mapToDTO(saved);
    }

    @Override
    public List<InvitationDTO> getReceivedInvitations(Integer userId) {
        return invitationRepository.findByReceiverId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvitationDTO> getSentInvitations(Integer userId) {
        return invitationRepository.findBySenderId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvitationDTO> getSentInvitationsForBoard(Integer userId, Integer boardId) {
        return invitationRepository.findBySenderIdAndBoardId(userId, boardId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InvitationDTO respondInvitation(Integer invitationId, Integer receiverId, boolean accept) throws InfyPintrestException {
        Optional<Invitation> invOpt = invitationRepository.findByIdAndReceiverId(invitationId, receiverId);
        if (invOpt.isEmpty()) {
            throw new InfyPintrestException("Service.INVITATION_NOT_FOUND");
        }
        Invitation inv = invOpt.get();

        inv.setStatus(accept ? InvitationStatus.ACCEPTED : InvitationStatus.DECLINED);
        
        // If accepted, add user as collaborator to the board
        if (accept && inv.getBoard() != null) {
            Board board = inv.getBoard();
            User receiver = inv.getReceiver();
            if (board.getCollaborators() == null) {
                board.setCollaborators(new java.util.ArrayList<>());
            }
            if (!board.getCollaborators().contains(receiver)) {
                board.getCollaborators().add(receiver);
                boardRepository.save(board);
            }
        }

        Invitation saved = invitationRepository.save(inv);
        return mapToDTO(saved);
    }

    @Override
    public void cancelInvitation(Integer invitationId, Integer senderId) throws InfyPintrestException {
        Optional<Invitation> invOpt = invitationRepository.findById(invitationId);
        if (invOpt.isEmpty()) {
            throw new InfyPintrestException("Service.INVITATION_NOT_FOUND");
        }
        Invitation inv = invOpt.get();

        if (!inv.getSender().getId().equals(senderId)) {
            throw new InfyPintrestException("Service.NOT_AUTHORIZED");
        }
        invitationRepository.delete(inv);
    }

    @Override
    public List<com.infy.pintrest.dto.UserSummaryDTO> getBoardCollaborators(Integer boardId) throws InfyPintrestException {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board board = boardOpt.get();
        
        List<com.infy.pintrest.dto.UserSummaryDTO> collaborators = new java.util.ArrayList<>();
        if (board.getCollaborators() != null) {
            for (User user : board.getCollaborators()) {
                com.infy.pintrest.dto.UserSummaryDTO dto = new com.infy.pintrest.dto.UserSummaryDTO();
                dto.setId(user.getId());
                dto.setName(user.getName());
                dto.setFullName(user.getUsername());
                dto.setProfilePicUrl(user.getProfilePath());
                dto.setEmail(user.getEmail());
                collaborators.add(dto);
            }
        }
        return collaborators;
    }

    @Override
    public List<com.infy.pintrest.dto.BoardDTO> getCollabBoards(Integer userId) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Service.USER_NOT_FOUND");
        }
        
        // Find all boards where user is a collaborator
        List<Invitation> acceptedInvitations = invitationRepository.findByReceiverIdAndStatus(userId, InvitationStatus.ACCEPTED);
        
        List<com.infy.pintrest.dto.BoardDTO> boards = new java.util.ArrayList<>();
        for (Invitation inv : acceptedInvitations) {
            if (inv.getBoard() != null) {
                Board board = inv.getBoard();
                User owner = board.getOwner();
                com.infy.pintrest.dto.BoardDTO dto = new com.infy.pintrest.dto.BoardDTO();
                dto.setId(board.getId());
                dto.setTitle(board.getTitle());
                dto.setDescription(board.getDescription());
                dto.setCoverImageUrl(board.getCoverImageUrl());
                dto.setOwnerId(owner.getId());
                dto.setOwnerName(owner.getUsername() != null ? owner.getUsername() : owner.getName());
                dto.setOwnerAvatar(owner.getProfilePath());
                dto.setPinCount(board.getPins() != null ? board.getPins().size() : 0);
                boards.add(dto);
            }
        }
        return boards;
    }

    @Override
    public void removeCollaborator(Integer boardId, Integer collaboratorId, Integer requesterId) throws InfyPintrestException {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board board = boardOpt.get();
        
        // Check if requester is the owner or the collaborator themselves
        boolean isOwner = board.getOwner().getId().equals(requesterId);
        boolean isSelf = collaboratorId.equals(requesterId);
        
        if (!isOwner && !isSelf) {
            throw new InfyPintrestException("Service.NOT_AUTHORIZED");
        }
        
        if (board.getCollaborators() != null) {
            board.getCollaborators().removeIf(c -> c.getId().equals(collaboratorId));
            boardRepository.save(board);
        }
        
        // Also delete the accepted invitation
        List<Invitation> invitations = invitationRepository.findByReceiverIdAndStatus(collaboratorId, InvitationStatus.ACCEPTED);
        for (Invitation inv : invitations) {
            if (inv.getBoard() != null && inv.getBoard().getId().equals(boardId)) {
                invitationRepository.delete(inv);
                break;
            }
        }
    }
}
