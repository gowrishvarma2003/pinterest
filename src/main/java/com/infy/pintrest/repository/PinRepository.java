//package com.infy.pintrest.repository;
//
//public interface PinRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;



import org.springframework.data.repository.CrudRepository;

import com.infy.pinterest.enums.PinStatus;
import com.infy.pintrest.entity.Pin;

public interface PinRepository extends CrudRepository<Pin,Integer>{

List<Pin> findByUserId(Integer userId);

List<Pin> findByUserIdAndStatus(Integer userId, PinStatus status);

List<Pin> findByBoardId(Integer boardId);

// Get all published public pins for home feed
List<Pin> findByStatusAndIsPrivateFalseOrderByCreatedAtDesc(PinStatus status);

// Get all public pins ordered by creation date
List<Pin> findByIsPrivateFalseOrderByCreatedAtDesc();

// Get public pins for a specific user
List<Pin> findByUserIdAndIsPrivateFalse(Integer userId);

// Get public published pins for a specific user
List<Pin> findByUserIdAndIsPrivateFalseAndStatus(Integer userId, PinStatus status);

}