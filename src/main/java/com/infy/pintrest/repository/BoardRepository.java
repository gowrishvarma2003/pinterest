//package com.infy.pintrest.repository;
//
//public interface BoardRepository {
//
//}
package com.infy.pintrest.repository;





import java.util.List;



import org.springframework.data.repository.CrudRepository;



import com.infy.pintrest.entity.Board;



public interface BoardRepository extends CrudRepository<Board, Integer>{

List<Board> findByOwnerId(Integer ownerId);



//showcase boards of the owner

List<Board>findByOwnerIdAndShowcaseTrue(Integer ownerId);

}






















