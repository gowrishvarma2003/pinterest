//package com.infy.pintrest.repository;
//
//public interface UserRepository {
//
//}

package com.infy.pintrest.repository;

import java.util.Optional;



import org.springframework.data.repository.CrudRepository;



import com.infy.pintrest.entity.User;



public interface UserRepository extends CrudRepository<User,Integer>{

Optional<User> findByEmail(String email);

}
