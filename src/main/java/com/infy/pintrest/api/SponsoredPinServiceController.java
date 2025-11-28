package com.infy.pintrest.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.SponsoredPinDTO;
import com.infy.pintrest.service.SponsoredPinService;

@RestController
@RequestMapping("/api/ads")
public class SponsoredPinServiceController {

    private final SponsoredPinService service;

    public SponsoredPinServiceController(SponsoredPinService service) {
        super();
        this.service = service;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SponsoredPinDTO> createSponsoredPin(
            @RequestPart("ad") SponsoredPinDTO dto, @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createSponsoredPin(dto, file));
    }

    public ResponseEntity<List<SponsoredPinDTO>> getAds() {
        return ResponseEntity.ok(service.getActiveSponsoredPins());
    }
}
