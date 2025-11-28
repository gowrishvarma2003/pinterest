package com.infy.pintrest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.infy.pintrest.dto.UserSummaryDTO;
import com.infy.pintrest.entity.Follow;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.FollowRepository;
import com.infy.pintrest.repository.UserRepository;

public class FollowUserImpl implements FollowUser {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public String followUser(Integer followerId, Integer followingId) throws InfyPintrestException {
        if (followerId.equals(followingId)) {
            throw new InfyPintrestException("Service.CANNOT_FOLLOW_SELF");
        }

        Optional<User> followerOpt = userRepository.findById(followerId);
        if (followerOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }

        Optional<User> followingOpt = userRepository.findById(followingId);
        if (followingOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }

        Optional<Follow> existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        if (existing.isPresent()) {
            return "Already following";
        }

        Follow follow = new Follow();
        follow.setFollower(followerOpt.get());
        follow.setFollowing(followingOpt.get());

        followRepository.save(follow);

        return "Followed successfully";
    }

    @Override
    public String unfollowUser(Integer followerId, Integer followingId) {
        Optional<Follow> existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        if (existing.isEmpty()) {
            return "Not following";
        }

        followRepository.delete(existing.get());
        return "User unfollowed";
    }

    @Override
    public List<UserSummaryDTO> getFollowers(Integer userId) {
        List<Follow> followers = followRepository.findByFollowingId(userId);
        List<UserSummaryDTO> result = new ArrayList<>();
        for (Follow f : followers) {
            UserSummaryDTO dto = modelMapper.map(f.getFollower(), UserSummaryDTO.class);
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<UserSummaryDTO> getFollowing(Integer userId) {
        List<Follow> following = followRepository.findByFollowerId(userId);
        List<UserSummaryDTO> result = new ArrayList<>();
        for (Follow f : following) {
            UserSummaryDTO dto = modelMapper.map(f.getFollowing(), UserSummaryDTO.class);
            result.add(dto);
        }
        return result;
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer targetid) {
        Optional<Follow> followOpt = followRepository.findByFollowerIdAndFollowingId(followerId, targetid);
        return followOpt.isPresent();
    }
}
