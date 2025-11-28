//package com.infy.pintrest.repository;
//
//public interface SavedPinRepository {
//
//}

package com.infy.pintrest.repository;



import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;



import com.infy.pintrest.entity.SavedPin;



public interface SavedPinRepository extends JpaRepository<SavedPin,Long> {



boolean existsByUserIdAndPinId(Integer userId, Integer pinId);



List<SavedPin> findByUserIdAndPinId(Integer userId, Integer pinId);



List<SavedPin> findByUserId(Integer userId);





}