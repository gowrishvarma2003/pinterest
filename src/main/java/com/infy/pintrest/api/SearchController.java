package com.infy.pintrest.api;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.pintrest.dto.PinViewDTO;
import com.infy.pintrest.entity.Board;
import com.infy.pintrest.entity.Pin;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.repository.BoardRepository;
import com.infy.pintrest.repository.PinRepository;
import com.infy.pintrest.repository.UserRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private PinRepository pinRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private PinViewDTO convertPinToViewDTO(Pin pin) {
        PinViewDTO dto = new PinViewDTO();
        dto.setId(pin.getId());
        dto.setTitle(pin.getTitle());
        dto.setDescription(pin.getDescription());
        dto.setImageUrl(pin.getImageUrl());
        dto.setVideoUrl(pin.getVideoUrl());
        dto.setSourceUrl(pin.getSourceUrl());
        dto.setAttribution(pin.getAttribution());
        dto.setKeywords(pin.getKeywords());
        dto.setTopics(pin.getTopics());
        dto.setProductTags(pin.getProductTags());
        dto.setPrivate(pin.isPrivate());
        dto.setStatus(pin.getStatus());
        dto.setCreatedAt(pin.getCreatedAt());
        dto.setUpdatedAt(pin.getUpdatedAt());
        
        if (pin.getBoard() != null) {
            dto.setBoardId(pin.getBoard().getId());
            dto.setBoardTitle(pin.getBoard().getTitle());
        }
        
        if (pin.getUser() != null) {
            dto.setUserId(pin.getUser().getId());
            dto.setUserName(pin.getUser().getName());
            dto.setUserFullname(pin.getUser().getUsername());
            dto.setUserProfilePath(pin.getUser().getProfilePath());
        }
        
        return dto;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        Map<String, Object> results = new HashMap<>();
        
        if (q == null || q.trim().isEmpty()) {
            results.put("pins", List.of());
            results.put("boards", List.of());
            results.put("users", List.of());
            return ResponseEntity.ok(results);
        }

        String query = q.trim();

        // Search pins
        List<Pin> pins = pinRepository.searchPublicPins(query);
        List<PinViewDTO> pinDTOs = pins.stream()
                .map(this::convertPinToViewDTO)
                .collect(Collectors.toList());
        results.put("pins", pinDTOs);

        // Search boards
        List<Board> boards = boardRepository.searchPublicBoards(query);
        List<Map<String, Object>> boardResults = boards.stream()
                .map(board -> {
                    Map<String, Object> boardMap = new HashMap<>();
                    boardMap.put("id", board.getId());
                    boardMap.put("title", board.getTitle());
                    boardMap.put("description", board.getDescription());
                    boardMap.put("coverImageUrl", board.getCoverImageUrl());
                    boardMap.put("pinCount", board.getPins() != null ? board.getPins().size() : 0);
                    
                    // Get owner info
                    User owner = board.getOwner();
                    if (owner != null) {
                        boardMap.put("ownerId", owner.getId());
                        boardMap.put("ownerName", owner.getName());
                        boardMap.put("ownerAvatar", owner.getProfilePath());
                    }
                    return boardMap;
                })
                .collect(Collectors.toList());
        results.put("boards", boardResults);

        // Search users
        List<User> users = userRepository.searchUsers(query);
        List<Map<String, Object>> userResults = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("username", user.getUsername() != null ? user.getUsername() : user.getName());
                    userMap.put("avatar", user.getProfilePath());
                    userMap.put("accountType", user.getAccountType());
                    // Get business info from business profile if available
                    if (user.getBusinessProfile() != null) {
                        userMap.put("businessName", user.getBusinessProfile().getBusinessName());
                        userMap.put("websiteUrl", user.getBusinessProfile().getWebsiteUrl());
                    }
                    return userMap;
                })
                .collect(Collectors.toList());
        results.put("users", userResults);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/pins")
    public ResponseEntity<List<PinViewDTO>> searchPins(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Pin> pins = pinRepository.searchPublicPins(q.trim());
        List<PinViewDTO> pinDTOs = pins.stream()
                .map(this::convertPinToViewDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pinDTOs);
    }

    @GetMapping("/boards")
    public ResponseEntity<List<Map<String, Object>>> searchBoards(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Board> boards = boardRepository.searchPublicBoards(q.trim());
        List<Map<String, Object>> boardResults = boards.stream()
                .map(board -> {
                    Map<String, Object> boardMap = new HashMap<>();
                    boardMap.put("id", board.getId());
                    boardMap.put("title", board.getTitle());
                    boardMap.put("description", board.getDescription());
                    boardMap.put("coverImageUrl", board.getCoverImageUrl());
                    boardMap.put("pinCount", board.getPins() != null ? board.getPins().size() : 0);
                    
                    User owner = board.getOwner();
                    if (owner != null) {
                        boardMap.put("ownerId", owner.getId());
                        boardMap.put("ownerName", owner.getName());
                        boardMap.put("ownerAvatar", owner.getProfilePath());
                    }
                    return boardMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(boardResults);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<User> users = userRepository.searchUsers(q.trim());
        List<Map<String, Object>> userResults = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("username", user.getUsername() != null ? user.getUsername() : user.getName());
                    userMap.put("avatar", user.getProfilePath());
                    userMap.put("accountType", user.getAccountType());
                    if (user.getBusinessProfile() != null) {
                        userMap.put("businessName", user.getBusinessProfile().getBusinessName());
                        userMap.put("websiteUrl", user.getBusinessProfile().getWebsiteUrl());
                    }
                    return userMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResults);
    }
}
