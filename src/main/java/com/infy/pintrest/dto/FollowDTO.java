//package com.infy.pintrest.dto;
//
//public class FollowDTO {
//
//}
package com.infy.pintrest.dto;



import lombok.Data;



@Data

public class FollowDTO {

private Integer id;

private Integer followerId;

private Integer followingId;

private String followedOn;



}

