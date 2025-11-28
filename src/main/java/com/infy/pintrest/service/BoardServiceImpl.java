package com.infy.pintrest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.BoardDTO;
import com.infy.pintrest.dto.BoardUpdateDTO;
import com.infy.pintrest.entity.Board;
import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.BoardRepository;
import com.infy.pintrest.repository.UserRepository;
import com.infy.pintrest.utility.HelperFunctions;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper = new ModelMapper();

    HelperFunctions hp = new HelperFunctions();

    private String root = System.getProperty("user.dir");
    private String uploadDir = root + "/uploads/boards";

    @Override
    public BoardDTO createBoard(BoardDTO boardDto, MultipartFile file) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(boardDto.getOwnerId());
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }
        User owner = userOpt.get();
        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setDescription(boardDto.getDescription());
        board.setIsPrivate(boardDto.isPrivate());
        board.setCoverImageUrl(boardDto.getCoverImageUrl());
        board.setOwner(owner);

        if (file != null && !file.isEmpty()) {
            String savedPath = hp.saveFile(file, uploadDir);
            board.setCoverImageUrl(savedPath);
        }

        Board savedBoard = boardRepository.save(board);

        BoardDTO dto = modelMapper.map(savedBoard, BoardDTO.class);

        dto.setPinCount(savedBoard.getPins() != null ? savedBoard.getPins().size() : 0);

        return dto;
    }

    @Override
    public List<BoardDTO> getBoardForUser(Integer userId) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }
        List<Board> boards = boardRepository.findByOwnerId(userId);
        List<BoardDTO> boardDtos = new ArrayList<>();

        for (Board board : boards) {
            BoardDTO dto = modelMapper.map(board, BoardDTO.class);
            dto.setPinCount(board.getPins() != null ? board.getPins().size() : 0);
            if (board.getOwner() != null) {
                dto.setOwnerId(board.getOwner().getId());
                dto.setOwnerName(board.getOwner().getFullname());
                dto.setOwnerAvatar(board.getOwner().getProfilePath());
            }
            boardDtos.add(dto);
        }
        return boardDtos;
    }

    @Override
    public List<BoardDTO> getPublicBoardsForUser(Integer userId) throws InfyPintrestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Auth.UserNotExists");
        }
        List<Board> boards = boardRepository.findByOwnerIdAndIsPrivateFalse(userId);
        List<BoardDTO> boardDtos = new ArrayList<>();

        for (Board board : boards) {
            BoardDTO dto = modelMapper.map(board, BoardDTO.class);
            dto.setPinCount(board.getPins() != null ? board.getPins().size() : 0);
            if (board.getOwner() != null) {
                dto.setOwnerId(board.getOwner().getId());
                dto.setOwnerName(board.getOwner().getFullname());
                dto.setOwnerAvatar(board.getOwner().getProfilePath());
            }
            boardDtos.add(dto);
        }
        return boardDtos;
    }

    @Override
    public BoardDTO getBoardById(Integer boardId) throws InfyPintrestException {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board board = boardOpt.get();
        BoardDTO dto = modelMapper.map(board, BoardDTO.class);
        dto.setPinCount(board.getPins() != null ? board.getPins().size() : 0);
        // Set owner info
        if (board.getOwner() != null) {
            dto.setOwnerId(board.getOwner().getId());
            dto.setOwnerName(board.getOwner().getFullname());
            dto.setOwnerAvatar(board.getOwner().getProfilePath());
        }
        return dto;
    }

    @Override
    public BoardDTO updateBoard(Integer boardId, BoardUpdateDTO boardUpdateDTO) throws InfyPintrestException {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board board = boardOpt.get();

        if (boardUpdateDTO.getTitle() != null) board.setTitle(boardUpdateDTO.getTitle());
        if (boardUpdateDTO.getDescription() != null) board.setDescription(boardUpdateDTO.getDescription());
        if (boardUpdateDTO.getIsPrivate() != null) board.setIsPrivate(boardUpdateDTO.getIsPrivate());
        if (boardUpdateDTO.getCoverImageUrl() != null) board.setCoverImageUrl(boardUpdateDTO.getCoverImageUrl());

        Board updated = boardRepository.save(board);
        BoardDTO boardDto = modelMapper.map(updated, BoardDTO.class);
        return boardDto;
    }

    @Override
    public void deleteBoard(Integer boardId) throws InfyPintrestException {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BOARD_NOT_FOUND");
        }
        Board board = boardOpt.get();

        if (board.getPins() != null) {
            for (Pin p : board.getPins()) {
                p.setBoard(null);
            }
        }

        boardRepository.delete(board);
    }
}
