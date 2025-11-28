//package com.infy.pintrest.dto;
//
//public class BoardUpdateDTO {
//
//}

package com.infy.pintrest.dto;



import lombok.Data;



@Data
public class BoardUpdateDTO {

private String title;

private String description;

private Boolean isPrivate;

private String coverImageUrl;

}