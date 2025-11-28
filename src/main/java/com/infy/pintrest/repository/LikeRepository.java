//package com.infy.pintrest.repository;
//
//public interface LikeRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;



import com.infy.pintrest.entity.PinLike;



public interface LikeRepository extends JpaRepository<PinLike,Integer> {

boolean existsByUserIdAndPinId(Integer userId, Integer pinId);

List<PinLike> findByUserId(Integer userId);

int countByPinId(Integer pinId);



}