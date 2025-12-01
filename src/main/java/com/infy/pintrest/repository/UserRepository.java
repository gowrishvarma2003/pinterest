//package com.infy.pintrest.repository;
//
//public interface UserRepository {
//
//}

package com.infy.pintrest.repository;

import java.util.List;
import java.util.Optional;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;



import com.infy.pintrest.entity.User;



public interface UserRepository extends CrudRepository<User,Integer>{

Optional<User> findByEmail(String email);

Optional<User> findByUsername(String username);

boolean existsByEmail(String email);

boolean existsByUsername(String username);

// Search users by name, username, or bio
@Query("SELECT u FROM User u WHERE " +
       "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(u.bio) LIKE LOWER(CONCAT('%', :query, '%'))")
List<User> searchUsers(@Param("query") String query);

}
