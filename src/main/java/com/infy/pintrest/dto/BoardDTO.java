package com.infy.pintrest.dto;

import lombok.Data;

@Data
public class BoardDTO {
    private Integer id;
    private String title;
    private String description;
    private boolean isPrivate;
    private Integer ownerId;
    private String ownerName;
    private String ownerAvatar;
    private String coverImageUrl;
    private Integer pinCount;
}





