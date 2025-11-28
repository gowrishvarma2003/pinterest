package com.infy.pintrest.dto;

import com.infy.pinterest.enums.PinStatus;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PinDTO {

private Integer id;

private String title;

private String description;

private String imageUrl;

private String videoUrl;

private String sourceUrl;

private String keywords;

private String topics;

private String productTags;

private String attribution;

private boolean isPrivate;

private boolean draft;

private String mediaType;

private PinStatus status;

private Integer userId;

private Integer boardId;



private MultipartFile file;



}

