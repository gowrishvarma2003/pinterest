package com.infy.pintrest.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.service.LikePinServiceImpl;

@RestController
@RequestMapping("api/pins")
@CrossOrigin
public class LikeController {

    @Autowired
    private LikePinServiceImpl likeService;

    @PostMapping("/like")
    public ResponseEntity<String> likePin(@RequestParam Integer userId, @RequestParam Integer pinId) {
        return ResponseEntity.ok(likeService.likePin(userId, pinId));
    }

    @GetMapping("/{pinId}/likes")
    public ResponseEntity<Integer> getLikes(@PathVariable Integer pinId) {
        return ResponseEntity.ok(likeService.getLikes(pinId));
    }

    @GetMapping("/isLiked")
    public ResponseEntity<Boolean> isLiked(@RequestParam Integer userId, @RequestParam Integer pinId) {
        return ResponseEntity.ok(likeService.isLiked(userId, pinId));
    }

    @GetMapping("/liked")
    public ResponseEntity<List<Pin>> getLikedPins(@RequestParam Integer userId) {
        return ResponseEntity.ok(likeService.getLikedPins(userId));
    }
}
