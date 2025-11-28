package com.infy.pintrest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private String root = System.getProperty("user.dir");
    private String uploadDir = root + "/uploads/pins";

    private ModelMapper modelMapper = new ModelMapper();

    HelperFunctions hp = new HelperFunctions();

    @Override
    public PinDTO createPin(PinDTO pinDto, MultipartFile file) throws InfyPintrestException {
        Optional<User> user = userRepository.findById(pinDto.getUserId());
        user.orElseThrow(() -> new InfyPintrestException("Auth.UserExists"));

        User newUser = user.get();

        Board board = null;
        if (pinDto.getBoardId() != null) {
            Optional<Board> b = boardRepository.findById(pinDto.getBoardId());
            b.orElseThrow(() -> new InfyPintrestException("na"));
            board = b.get();
        }

        String savePath = null;
        if (file != null && !file.isEmpty()) {
            savePath = hp.saveFile(file, uploadDir);
        }

        Pin pin = new Pin();
        pin.setTitle(pinDto.getTitle());
        pin.setDescription(pinDto.getDescription());
        pin.setSourceUrl(pinDto.getSourceUrl());
        pin.setKeywords(pinDto.getKeywords());
        pin.setPrivate(pinDto.isPrivate());
        pin.setUser(newUser);
        pin.setBoard(board);

        if (savePath != null) {
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image")) {
                pin.setImageUrl(savePath);
            } else if (contentType != null && contentType.startsWith("video")) {
                pin.setVideoUrl(savePath);
            }
        }

        pin.setLikes(0);
        Pin savedPin = pinRepository.save(pin);

        PinDTO result = new PinDTO();
        result.setId(savedPin.getId());
        result.setTitle(savedPin.getTitle());
        result.setDescription(savedPin.getDescription());
        result.setSourceUrl(savedPin.getSourceUrl());
        result.setKeywords(savedPin.getKeywords());
        result.setPrivate(savedPin.isPrivate());
        result.setUserId(savedPin.getUser().getId());

        if (savedPin.getBoard() != null) {
            result.setBoardId(savedPin.getBoard().getId());
        }

        result.setImageUrl(savedPin.getImageUrl());
        result.setVideoUrl(savedPin.getVideoUrl());

        return result;
    }

    @Override
    public List<PinViewDTO> getPinsForUser(Integer userId) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }

        List<Pin> pins = pinRepository.findByUserId(userId);
        List<PinViewDTO> result = new ArrayList<>();

        for (Pin pin : pins) {
            PinViewDTO dto = convertToDTO(pin);
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<PinViewDTO> getPinsForBoard(Integer boardId) throws InfyPintrestException {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }

        List<Pin> pins = pinRepository.findByBoardId(boardId);
        List<PinViewDTO> result = new ArrayList<>();

        for (Pin pin : pins) {
            PinViewDTO dto = convertToDTO(pin);
            result.add(dto);
        }

        return result;
    }

    @Override
    public PinViewDTO getPinDetails(Integer pinId) throws InfyPintrestException {
        Optional<Pin> pinOpt = pinRepository.findById(pinId);
        if (pinOpt.isEmpty()) {
            throw new InfyPintrestException("Service.PIN_NOT_FOUND ");
        }
        Pin pin = pinOpt.get();
        return convertToDTO(pin);
    }

    @Override
    public PinViewDTO movePin(Integer pinId, Integer targetBoardId) throws InfyPintrestException {
        Optional<Pin> pinOpt = pinRepository.findById(pinId);
        if (pinOpt.isEmpty()) {
            throw new InfyPintrestException("Service.PIN_NOT_FOUND ");
        }
        Pin pin = pinOpt.get();

        Optional<Board> boardOpt = boardRepository.findById(targetBoardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board targetBoard = boardOpt.get();
        pin.setBoard(targetBoard);
        Pin updatePin = pinRepository.save(pin);

        return convertToDTO(updatePin);
    }

    @Override
    public void deletePin(Integer pinId) throws InfyPintrestException {
        Optional<Pin> pinOpt = pinRepository.findById(pinId);
        if (pinOpt.isEmpty()) {
            throw new InfyPintrestException("Service.PIN_NOT_FOUND ");
        }
        Pin pin = pinOpt.get();

        deleteFileExists(pin.getImageUrl());
        deleteFileExists(pin.getVideoUrl());

        pinRepository.delete(pin);
    }

    // ------- Helper functions -------

    private PinViewDTO convertToDTO(Pin pin) {
        PinViewDTO dto = modelMapper.map(pin, PinViewDTO.class);

        if (pin.getBoard() != null) {
            dto.setBoardId(pin.getBoard().getId());
            dto.setBoardTitle(uploadDir);
        }

        dto.setUserId(pin.getUser().getId());

        return dto;
    }

    private void deleteFileExists(String path) throws InfyPintrestException {
        try {
            if (path != null) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(path);
                java.nio.file.Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            throw new InfyPintrestException(e.getMessage());
        }
    }
}
