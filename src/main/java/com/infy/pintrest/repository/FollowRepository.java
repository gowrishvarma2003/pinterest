//package com.infy.pintrest.repository;
//
//public interface FollowRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;

import java.util.Optional;



import org.springframework.data.repository.CrudRepository;



import com.infy.pintrest.entity.Follow;



public interface FollowRepository extends CrudRepository<Follow, Integer>{

Optional<Follow> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

List<Follow> findByFollowerId(Integer followerId);

List<Follow> findByFollowingId(Integer followingId);

}