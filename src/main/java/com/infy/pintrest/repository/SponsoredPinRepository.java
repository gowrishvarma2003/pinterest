//package com.infy.pintrest.repository;
//
//public interface SponsoredPinRepository {
//
//}
package com.infy.pintrest.repository;



import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



import com.infy.pintrest.entity.SponsoredPin;



@Repository

public interface SponsoredPinRepository extends JpaRepository<SponsoredPin, Long>{



List<SponsoredPin> findByActiveTrue();



}