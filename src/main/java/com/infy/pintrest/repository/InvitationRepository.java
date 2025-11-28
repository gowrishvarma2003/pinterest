package com.infy.pintrest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.pinterest.enums.InvitationStatus;
import com.infy.pintrest.entity.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    List<Invitation> findByReceiverId(Integer receiverId);
    List<Invitation> findBySenderId(Integer senderId);
    List<Invitation> findBySenderIdAndBoardId(Integer senderId, Integer boardId);
    Optional<Invitation> findByIdAndReceiverId(Integer id, Integer receiverId);
    Optional<Invitation> findBySenderIdAndReceiverIdAndBoardId(Integer senderId, Integer receiverId, Integer boardId);
    List<Invitation> findByReceiverIdAndStatus(Integer receiverId, InvitationStatus status);
}
