package com.infy.pintrest.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.pintrest.dto.UserSummaryDTO;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.FollowUser;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowUser followUser;

    @PostMapping("/{followerId}/follow/{followingId}")
    public ResponseEntity<String> follow(@PathVariable Integer followerId, @PathVariable Integer followingId) throws InfyPintrestException {
        String res = followUser.followUser(followerId, followingId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public ResponseEntity<String> unfollow(@PathVariable Integer followerId, @PathVariable Integer followingId) throws InfyPintrestException {
        String res = followUser.unfollowUser(followerId, followingId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSummaryDTO>> followers(@PathVariable Integer userId) throws InfyPintrestException {
        return ResponseEntity.ok(followUser.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSummaryDTO>> following(@PathVariable Integer userId) throws InfyPintrestException {
        return ResponseEntity.ok(followUser.getFollowing(userId));
    }

    @GetMapping("/{followerId}/isfollowing/{targetId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Integer followerId, @PathVariable Integer targetId) throws InfyPintrestException {
        return ResponseEntity.ok(followUser.isFollowing(followerId, targetId));
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Integer> followersCount(@PathVariable Integer userId) {
        return ResponseEntity.ok(followUser.getFollowersCount(userId));
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Integer> followingCount(@PathVariable Integer userId) {
        return ResponseEntity.ok(followUser.getFollowingCount(userId));
    }
}
