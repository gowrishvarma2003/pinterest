package com.infy.pintrest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.pintrest.entity.User;
import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.entity.PinLike;
import com.infy.pintrest.repository.LikeRepository;
import com.infy.pintrest.repository.PinRepository;
import com.infy.pintrest.repository.UserRepository;

@Service
@Transactional
public class LikePinServiceImpl implements PinLikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PinRepository pinRepository;

    public String likePin(Integer userId, Integer pinId) {
        if (likeRepository.existsByUserIdAndPinId(userId, pinId)) {
            return "You already liked this pin";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new RuntimeException("Pin not found"));

        PinLike like = new PinLike();
        like.setUser(user);
        like.setPin(pin);

        likeRepository.save(like);

        // increase pin like count
        pin.setLikes(pin.getLikes() + 1);
        pinRepository.save(pin);

        return "Pin liked successfully";
    }

    public int getLikes(Integer pinId) {
        return likeRepository.countByPinId(pinId);
    }

    public List<Pin> getLikedPins(Integer userId) {
        return likeRepository.findByUserId(userId).stream()
                .map(PinLike::getPin)
                .collect(Collectors.toList());
    }
}
