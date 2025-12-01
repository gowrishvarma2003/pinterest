//package com.infy.pintrest.repository;
//
//public interface BoardRepository {
//
//}
package com.infy.pintrest.repository;





import java.util.List;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;



import com.infy.pintrest.entity.Board;



public interface BoardRepository extends CrudRepository<Board, Integer>{

List<Board> findByOwnerId(Integer ownerId);



//showcase boards of the owner

List<Board>findByOwnerIdAndShowcaseTrue(Integer ownerId);

// Get public boards for a user
List<Board> findByOwnerIdAndIsPrivateFalse(Integer ownerId);

// Search public boards by title or description
@Query("SELECT b FROM Board b WHERE (b.isPrivate = false OR b.isPrivate IS NULL) AND " +
       "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')))")
List<Board> searchPublicBoards(@Param("query") String query);

}






















