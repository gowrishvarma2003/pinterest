//package com.infy.pintrest.repository;
//
//public interface PinRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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

// Search public pins by title, description, or keywords
@Query("SELECT p FROM Pin p WHERE p.isPrivate = false AND p.status = 'PUBLISHED' AND " +
       "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(p.keywords) LIKE LOWER(CONCAT('%', :query, '%')))")
List<Pin> searchPublicPins(@Param("query") String query);

// Get public pins from business accounts
@Query("SELECT p FROM Pin p WHERE p.isPrivate = false AND p.user.accountType = 'BUSINESS' ORDER BY p.createdAt DESC")
List<Pin> findBusinessAccountPins();

// Get public pins by category/topic
@Query("SELECT p FROM Pin p WHERE p.isPrivate = false AND p.status = 'PUBLISHED' AND " +
       "LOWER(p.topics) LIKE LOWER(CONCAT('%', :category, '%')) ORDER BY p.createdAt DESC")
List<Pin> findByCategory(@Param("category") String category);

}