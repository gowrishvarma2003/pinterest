package com.infy.pintrest.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.pintrest.dto.PinDTO;
import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.entity.SavedPin;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.repository.PinRepository;
import com.infy.pintrest.repository.SavedPinRepository;
import com.infy.pintrest.repository.UserRepository;

@Service
public class PinInteractionServiceImpl implements PinInteractionService {

    @Autowired
    private SavedPinRepository savedPinRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PinRepository pinRepository;

    @Override
    public String savePin(Integer userId, Integer pinId) {
        if (savedPinRepository.existsByUserIdAndPinId(userId, pinId)) {
            return "Pin already saved";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new RuntimeException("Pin not found"));

        SavedPin saved = new SavedPin();
        saved.setUser(user);
        saved.setPin(pin);

        savedPinRepository.save(saved);

        return "Pin saved successfully";
    }

    @Override
    public List<Pin> getSavedPins(Integer userId) {
        return savedPinRepository.findByUserId(userId)
                .stream()
                .map(SavedPin::getPin)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSaved(Integer userId, Integer pinId) {
        return savedPinRepository.existsByUserIdAndPinId(userId, pinId);
    }
}
