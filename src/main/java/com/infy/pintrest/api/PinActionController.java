package com.infy.pintrest.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.service.PinInteractionService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/pins")
public class PinActionController {

    @Autowired
    private PinInteractionService service;

    @PostMapping("/save")
    public ResponseEntity<String> savePin(@RequestParam Integer userId, @RequestParam Integer pinId) {
        return ResponseEntity.ok(service.savePin(userId, pinId));
    }

    @GetMapping("/isSaved")
    public ResponseEntity<Boolean> isSaved(@RequestParam Integer userId, @RequestParam Integer pinId) {
        return ResponseEntity.ok(service.isSaved(userId, pinId));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<Pin>> getSavedPins(@RequestParam Integer userId) {
        return ResponseEntity.ok(service.getSavedPins(userId));
    }
}
