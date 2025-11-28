//package com.infy.pintrest.repository;
//
//public interface InvitationRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;

import java.util.Optional;



import org.springframework.data.jpa.repository.JpaRepository;



import com.infy.pintrest.entity.Invitation;



public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

List<Invitation> findByReceiverId(Integer receiverId);

List<Invitation>findBySenderId(Integer senderId);

Optional<Invitation> findByIdAndReceiverId(Integer id,Integer receiverId);



}
