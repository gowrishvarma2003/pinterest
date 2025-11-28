//package com.infy.pintrest.repository;
//
//public interface BusinesProfileRepository {
//
//}

package com.infy.pintrest.repository;



import java.util.Optional;



import org.springframework.data.jpa.repository.JpaRepository;



import com.infy.pintrest.entity.BusinessProfile;



public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Integer>{



Optional<BusinessProfile>findByUserId(Integer userId);



}
