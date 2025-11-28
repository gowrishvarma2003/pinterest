package com.infy.pintrest.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.pintrest.dto.BusinessProfileViewDTO;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.BusinessService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/business")
public class BusinessProfileController {

    private final BusinessService businessService;

    public BusinessProfileController(BusinessService businessService) {
        this.businessService = businessService;
    }

    // Get a single business profile (by user id)
    @GetMapping("/{userId}")
    public ResponseEntity<BusinessProfileViewDTO> getBusinessProfile(@PathVariable Integer userId) throws InfyPintrestException {
        BusinessProfileViewDTO dto = businessService.getBusinessProfileByUserId(userId);
        return ResponseEntity.ok(dto);
    }

    // Get all business profiles (discovery)
    @GetMapping
    public ResponseEntity<List<BusinessProfileViewDTO>> getAllBusinessProfiles() {
        List<BusinessProfileViewDTO> list = businessService.getAllBusinessProfiles();
        return ResponseEntity.ok(list);
    }
}
