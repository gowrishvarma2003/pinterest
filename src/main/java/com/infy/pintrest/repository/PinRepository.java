//package com.infy.pintrest.repository;
//
//public interface PinRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;



import org.springframework.data.repository.CrudRepository;



import com.infy.pintrest.entity.Pin;



public interface PinRepository extends CrudRepository<Pin,Integer>{

List<Pin> findByUserId(Integer userId);

List<Pin> findByBoardId(Integer boardId);

}