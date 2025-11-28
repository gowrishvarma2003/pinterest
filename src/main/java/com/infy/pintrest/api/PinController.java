package com.infy.pintrest.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.infy.pinterest.enums.PinStatus;
import com.infy.pintrest.dto.PinDTO;
import com.infy.pintrest.dto.PinViewDTO;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.PinService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("pins")
public class PinController {

    @Autowired
    private PinService pinService;

    @PostMapping(value = "createpin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PinDTO> createPin(@RequestPart("pin") PinDTO pinDto,
                                            @RequestPart(value = "file", required = false) MultipartFile file) throws InfyPintrestException {
        PinDTO saved = pinService.createPin(pinDto, file);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{pinId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PinDTO> updatePin(@PathVariable Integer pinId,
                                            @RequestPart("pin") PinDTO pinDto,
                                            @RequestPart(value = "file", required = false) MultipartFile file) throws InfyPintrestException {
        PinDTO updated = pinService.updatePin(pinId, pinDto, file);
        return ResponseEntity.ok(updated);
    }

    // @PostMapping(value="createpin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<PinDTO> createPin(@RequestBody PinDTO pinDto) throws InfyPintrestException{
    // MultipartFile file = pinDto.getFile();
    // PinDTO saved = pinService.createPin(pinDto, file);
    // return new ResponseEntity<>(saved,HttpStatus.CREATED);
    // }

    @GetMapping("/users/{userId}/pins")
    public ResponseEntity<List<PinViewDTO>> getPindsForUser(@PathVariable Integer userId,
            @RequestParam(value = "status", required = false) PinStatus status) throws InfyPintrestException {
        List<PinViewDTO> pins = pinService.getPinsForUser(userId, status);
        return ResponseEntity.ok(pins);
    }

    @GetMapping("/users/{userId}/pins/public")
    public ResponseEntity<List<PinViewDTO>> getPublicPinsForUser(@PathVariable Integer userId) throws InfyPintrestException {
        List<PinViewDTO> pins = pinService.getPublicPinsForUser(userId);
        return ResponseEntity.ok(pins);
    }

    @GetMapping("/boards/{boardId}/pins")
    public ResponseEntity<List<PinViewDTO>> getPinsForBoard(@PathVariable Integer boardId) throws InfyPintrestException {
        List<PinViewDTO> pins = pinService.getPinsForBoard(boardId);
        return ResponseEntity.ok(pins);
    }

    @GetMapping("/{pinId}")
    public ResponseEntity<PinViewDTO> getPinDetails(@PathVariable Integer pinId) throws InfyPintrestException {
        PinViewDTO pin = pinService.getPinDetails(pinId);
        return ResponseEntity.ok(pin);
    }

    @PutMapping("/{pinId}/move")
    public ResponseEntity<PinViewDTO> movePin(@PathVariable Integer pinId, @RequestParam Integer targetBoardId) throws InfyPintrestException {
        PinViewDTO movedPin = pinService.movePin(pinId, targetBoardId);
        return ResponseEntity.ok(movedPin);
    }

    @DeleteMapping({"/{pinId}", "/deletepin/{pinId}"})
    public ResponseEntity<String> deletePin(@PathVariable Integer pinId) throws InfyPintrestException {
        pinService.deletePin(pinId);
        return ResponseEntity.ok("Pin deleted successfully");
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PinViewDTO>> getHomeFeedPins() throws InfyPintrestException {
        List<PinViewDTO> pins = pinService.getHomeFeedPins();
        return ResponseEntity.ok(pins);
    }
}
