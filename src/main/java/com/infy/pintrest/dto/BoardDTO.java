//package com.infy.pintrest.dto;
//
//public class BoardDTO {
//
//}
package com.infy.pintrest.dto;



import lombok.Data;



@Data
public class BoardDTO {

private Integer id;

private String title;

private String description;

private boolean isPrivate;

private Integer ownerId;

private String coverImageUrl;



private Integer pinCount;

}





