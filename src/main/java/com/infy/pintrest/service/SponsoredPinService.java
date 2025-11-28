//package com.infy.pintrest.service;
//
//public interface SponsoredPinService {
//
//}

package com.infy.pintrest.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.SponsoredPinDTO;

public interface SponsoredPinService {
    SponsoredPinDTO createSponsoredPin(SponsoredPinDTO dto, MultipartFile file);
    List<SponsoredPinDTO> getActiveSponsoredPins();

}
