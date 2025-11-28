package com.infy.pintrest.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    ModelMapper modelMapper = new ModelMapper();

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
        }

        Invitation inv = new Invitation();
        inv.setSender(sender);
        inv.setReceiver(receiver);
        inv.setBoard(board);
        inv.setStatus(InvitationStatus.PENDING);

        Invitation saved = invitationRepository.save(inv);

        InvitationDTO dto = modelMapper.map(saved, InvitationDTO.class);
        dto.setSenderName(sender.getName());
        if (board != null) {
            dto.setBoardTitle(board.getTitle());
        }
        return dto;
    }

    @Override
    public List<InvitationDTO> getReceivedInvitations(Integer userId) {
        return invitationRepository.findByReceiverId(userId)
                .stream()
                .map(inv -> {
                    InvitationDTO dto = modelMapper.map(inv, InvitationDTO.class);
                    dto.setSenderName(inv.getSender().getName());
                    if (inv.getBoard() != null) {
                        dto.setBoardTitle(inv.getBoard().getTitle());
                    }
                    return dto;
                })
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

        Invitation saved = invitationRepository.save(inv);

        InvitationDTO dto = modelMapper.map(saved, InvitationDTO.class);
        dto.setSenderName(saved.getSender().getName());
        if (saved.getBoard() != null) {
            dto.setBoardTitle(saved.getBoard().getTitle());
        }
        return dto;
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
}
