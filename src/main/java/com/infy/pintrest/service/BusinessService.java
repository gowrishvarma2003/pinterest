//package com.infy.pintrest.service;
//
//public interface BusinessService {
//
//}

package com.infy.pintrest.service;



import java.util.List;



import com.infy.pintrest.dto.BusinessProfileViewDTO;

import com.infy.pintrest.exception.InfyPintrestException;



public interface BusinessService {



BusinessProfileViewDTO getBusinessProfileByUserId(Integer businessUserId) throws InfyPintrestException;



List<BusinessProfileViewDTO> getAllBusinessProfiles();



}