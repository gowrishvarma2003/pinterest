package com.infy.pintrest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.pinterest.enums.PinStatus;
import com.infy.pintrest.dto.PinDTO;
import com.infy.pintrest.dto.PinViewDTO;
import com.infy.pintrest.entity.Board;
import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.BoardRepository;
import com.infy.pintrest.repository.PinRepository;
import com.infy.pintrest.repository.UserRepository;
import com.infy.pintrest.utility.HelperFunctions;

@Service
public class PinServiceImpl implements PinService {

    @Autowired
    private PinRepository pinRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/pins";

    private final ModelMapper modelMapper = new ModelMapper();

    private final HelperFunctions helper = new HelperFunctions();

    @Override
    public PinDTO createPin(PinDTO pinDto, MultipartFile file) throws InfyPintrestException {
        User owner = userRepository.findById(pinDto.getUserId())
                .orElseThrow(() -> new InfyPintrestException("Auth.UserNotExists"));

        Pin pin = new Pin();
        pin.setUser(owner);
        pin.setBoard(resolveBoard(pinDto.getBoardId()));
        pin.setLikes(0);

        applyDetails(pin, pinDto);
        applyMedia(pin, file, pinDto);

        Pin savedPin = pinRepository.save(pin);
        return mapToDto(savedPin);
    }

    @Override
    public PinDTO updatePin(Integer pinId, PinDTO pinDto, MultipartFile file) throws InfyPintrestException {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new InfyPintrestException("Service.PIN_NOT_FOUND"));

        if (pinDto.getBoardId() != null) {
            pin.setBoard(resolveBoard(pinDto.getBoardId()));
        }

        applyDetails(pin, pinDto);
        applyMedia(pin, file, pinDto);

        Pin updated = pinRepository.save(pin);
        return mapToDto(updated);
    }

    @Override
    public List<PinViewDTO> getPinsForUser(Integer userId, PinStatus status) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }

        List<Pin> pins = status != null
                ? pinRepository.findByUserIdAndStatus(userId, status)
                : pinRepository.findByUserId(userId);

        List<PinViewDTO> result = new ArrayList<>();
        for (Pin pin : pins) {
            result.add(convertToDTO(pin));
        }
        return result;
    }

    @Override
    public List<PinViewDTO> getPublicPinsForUser(Integer userId) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }

        // Get only public published pins for the user
        List<Pin> pins = pinRepository.findByUserIdAndIsPrivateFalseAndStatus(userId, PinStatus.PUBLISHED);
        
        // If no published pins, get all public pins
        if (pins.isEmpty()) {
            pins = pinRepository.findByUserIdAndIsPrivateFalse(userId);
        }

        List<PinViewDTO> result = new ArrayList<>();
        for (Pin pin : pins) {
            result.add(convertToDTO(pin));
        }
        return result;
    }

    @Override
    public List<PinViewDTO> getPinsForBoard(Integer boardId) throws InfyPintrestException {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new InfyPintrestException("Service.BOARD_NOT_FOUND"));

        List<Pin> pins = pinRepository.findByBoardId(boardId);
        List<PinViewDTO> result = new ArrayList<>();
        for (Pin pin : pins) {
            result.add(convertToDTO(pin));
        }
        return result;
    }

    @Override
    public PinViewDTO getPinDetails(Integer pinId) throws InfyPintrestException {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new InfyPintrestException("Service.PIN_NOT_FOUND"));
        return convertToDTO(pin);
    }

    @Override
    public PinViewDTO movePin(Integer pinId, Integer targetBoardId) throws InfyPintrestException {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new InfyPintrestException("Service.PIN_NOT_FOUND"));

        Board targetBoard = boardRepository.findById(targetBoardId)
                .orElseThrow(() -> new InfyPintrestException("Service.BOARD_NOT_FOUND"));

        pin.setBoard(targetBoard);
        Pin updated = pinRepository.save(pin);
        return convertToDTO(updated);
    }

    @Override
    public void deletePin(Integer pinId) throws InfyPintrestException {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new InfyPintrestException("Service.PIN_NOT_FOUND"));

        deleteLocalFileIfExists(pin.getImageUrl());
        deleteLocalFileIfExists(pin.getVideoUrl());

        pinRepository.delete(pin);
    }

    @Override
    public List<PinViewDTO> getHomeFeedPins() throws InfyPintrestException {
        // Get all published public pins for the home feed
        List<Pin> pins = pinRepository.findByStatusAndIsPrivateFalseOrderByCreatedAtDesc(PinStatus.PUBLISHED);
        
        // If no published pins, also include all public pins
        if (pins.isEmpty()) {
            pins = pinRepository.findByIsPrivateFalseOrderByCreatedAtDesc();
        }
        
        List<PinViewDTO> result = new ArrayList<>();
        for (Pin pin : pins) {
            result.add(convertToDTO(pin));
        }
        return result;
    }

    private PinViewDTO convertToDTO(Pin pin) {
        PinViewDTO dto = modelMapper.map(pin, PinViewDTO.class);

        if (pin.getBoard() != null) {
            dto.setBoardId(pin.getBoard().getId());
            dto.setBoardTitle(pin.getBoard().getTitle());
        }

        User user = pin.getUser();
        dto.setUserId(user.getId());
        dto.setUserName(user.getName());
        dto.setUserFullname(user.getFullname());
        dto.setUserProfilePath(user.getProfilePath());
        
        return dto;
    }

    private PinDTO mapToDto(Pin pin) {
        PinDTO dto = new PinDTO();
        dto.setId(pin.getId());
        dto.setTitle(pin.getTitle());
        dto.setDescription(pin.getDescription());
        dto.setSourceUrl(pin.getSourceUrl());
        dto.setKeywords(pin.getKeywords());
        dto.setTopics(pin.getTopics());
        dto.setProductTags(pin.getProductTags());
        dto.setAttribution(pin.getAttribution());
        dto.setPrivate(pin.isPrivate());
        dto.setStatus(pin.getStatus());
        dto.setDraft(pin.getStatus() == PinStatus.DRAFT);
        dto.setMediaType(pin.getVideoUrl() != null ? "VIDEO" : "IMAGE");
        dto.setUserId(pin.getUser().getId());

        if (pin.getBoard() != null) {
            dto.setBoardId(pin.getBoard().getId());
        }

        dto.setImageUrl(pin.getImageUrl());
        dto.setVideoUrl(pin.getVideoUrl());
        return dto;
    }

    private void applyDetails(Pin pin, PinDTO dto) {
        if (dto.getTitle() != null) {
            pin.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            pin.setDescription(dto.getDescription());
        }
        if (dto.getSourceUrl() != null) {
            pin.setSourceUrl(dto.getSourceUrl());
        }
        if (dto.getKeywords() != null) {
            pin.setKeywords(normalizeCsv(dto.getKeywords()));
        }
        if (dto.getTopics() != null) {
            pin.setTopics(normalizeCsv(dto.getTopics()));
        }
        if (dto.getProductTags() != null) {
            pin.setProductTags(normalizeCsv(dto.getProductTags()));
        }
        if (dto.getAttribution() != null) {
            pin.setAttribution(dto.getAttribution());
        }

        pin.setPrivate(dto.isPrivate());
        pin.setStatus(resolveStatus(dto, pin.getStatus()));
    }

    private void applyMedia(Pin pin, MultipartFile file, PinDTO dto) throws InfyPintrestException {
        if (file != null && !file.isEmpty()) {
            replaceWithUploadedFile(pin, file);
            return;
        }

        String imageValue = sanitize(dto.getImageUrl());
        String videoValue = sanitize(dto.getVideoUrl());

        if (imageValue == null && videoValue == null) {
            return;
        }

        if ("VIDEO".equalsIgnoreCase(dto.getMediaType())) {
            if (videoValue == null) {
                videoValue = imageValue;
            }
            if (videoValue != null) {
                pin.setVideoUrl(videoValue);
                pin.setImageUrl(null);
            }
        } else {
            if (imageValue == null) {
                imageValue = videoValue;
            }
            if (imageValue != null) {
                pin.setImageUrl(imageValue);
                pin.setVideoUrl(null);
            }
        }
    }

    private void replaceWithUploadedFile(Pin pin, MultipartFile file) throws InfyPintrestException {
        deleteLocalFileIfExists(pin.getImageUrl());
        deleteLocalFileIfExists(pin.getVideoUrl());

        String savedPath = helper.saveFile(file, uploadDir);
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video")) {
            pin.setVideoUrl(savedPath);
            pin.setImageUrl(null);
        } else {
            pin.setImageUrl(savedPath);
            pin.setVideoUrl(null);
        }
    }

    private Board resolveBoard(Integer boardId) throws InfyPintrestException {
        if (boardId == null) {
            return null;
        }
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new InfyPintrestException("Service.BOARD_NOT_FOUND"));
    }

    private PinStatus resolveStatus(PinDTO dto, PinStatus current) {
        if (dto.getStatus() != null) {
            return dto.getStatus();
        }
        if (dto.isDraft()) {
            return PinStatus.DRAFT;
        }
        if (current != null && !dto.isDraft()) {
            return current == PinStatus.DRAFT ? PinStatus.PUBLISHED : current;
        }
        return PinStatus.PUBLISHED;
    }

    private String normalizeCsv(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(segment -> !segment.isEmpty())
                .collect(Collectors.joining(","));
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void deleteLocalFileIfExists(String path) throws InfyPintrestException {
        try {
            if (path != null && !path.startsWith("http")) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(path);
                java.nio.file.Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            throw new InfyPintrestException(e.getMessage());
        }
    }
}
