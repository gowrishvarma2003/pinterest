//package com.infy.pintrest.service;
//
//public interface PinService {
//
//}

package com.infy.pintrest.service;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.infy.pinterest.enums.PinStatus;
import com.infy.pintrest.dto.PinDTO;
import com.infy.pintrest.dto.PinViewDTO;
import com.infy.pintrest.exception.InfyPintrestException;

public interface PinService {
    PinDTO createPin(PinDTO pinDto, MultipartFile file) throws InfyPintrestException;
    PinDTO updatePin(Integer pinId, PinDTO pinDto, MultipartFile file) throws InfyPintrestException;
    List<PinViewDTO> getPinsForUser(Integer userId, PinStatus status) throws InfyPintrestException;
    List<PinViewDTO> getPublicPinsForUser(Integer userId) throws InfyPintrestException;
    List<PinViewDTO> getPinsForBoard(Integer boardId) throws InfyPintrestException;
    PinViewDTO getPinDetails(Integer pinId) throws InfyPintrestException;
    PinViewDTO movePin(Integer pinId, Integer targetBoardId) throws InfyPintrestException;
    void deletePin(Integer pinId) throws InfyPintrestException;
    List<PinViewDTO> getHomeFeedPins() throws InfyPintrestException;
    List<PinViewDTO> getSponsoredPins() throws InfyPintrestException;
    List<PinViewDTO> getPinsByCategory(String category) throws InfyPintrestException;
}