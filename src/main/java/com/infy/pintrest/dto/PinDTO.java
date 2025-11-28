package com.infy.pintrest.dto;



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

private boolean isPrivate;

private Integer userId;

private Integer boardId;



private MultipartFile file;



}

