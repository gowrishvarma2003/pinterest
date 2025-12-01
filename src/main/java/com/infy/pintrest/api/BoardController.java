package com.infy.pintrest.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.BoardDTO;
import com.infy.pintrest.dto.BoardUpdateDTO;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.service.BoardService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @PostMapping(value = "/create_board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardDTO> createBoard(@RequestPart("board") BoardDTO boardDto, @RequestPart("file") MultipartFile file) throws InfyPintrestException {
        BoardDTO created = boardService.createBoard(boardDto, file);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/boards")
    public ResponseEntity<List<BoardDTO>> getBoardsForUsers(@PathVariable Integer userId) throws InfyPintrestException {
        List<BoardDTO> boards = boardService.getBoardForUser(userId);
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/users/{userId}/boards/public")
    public ResponseEntity<List<BoardDTO>> getPublicBoardsForUsers(@PathVariable Integer userId) throws InfyPintrestException {
        List<BoardDTO> boards = boardService.getPublicBoardsForUser(userId);
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardDTO> getBoardById(@PathVariable Integer boardId) throws InfyPintrestException {
        BoardDTO board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(board);
    }

    @PutMapping("/boards/{boardId}")
    public ResponseEntity<BoardDTO> updateBoard(@PathVariable Integer boardId, @RequestBody BoardUpdateDTO boardUpdateDTO) throws InfyPintrestException {
        BoardDTO updated = boardService.updateBoard(boardId, boardUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Integer boardId) throws InfyPintrestException {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok("Board deleted successfully");
    }
}
