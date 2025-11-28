//package com.infy.pintrest.dto;
//
//public class PinViewDTO {
//
//}


package com.infy.pintrest.dto;



import lombok.Data;



@Data
public class PinViewDTO {

private Integer id;

private String title;

private String description;



private String imageUrl;

private String videoUrl;



private String sourceUrl;



private Integer boardId;

private String boardTitle;



private Integer userId;

}
