//package com.infy.pintrest.dto;
//
//public class BusinesProfileViewDTO {
//
//}

package com.infy.pintrest.dto;



import java.util.List;



import lombok.Data;



@Data

public class BusinessProfileViewDTO {



private Integer businessProfileId;

private String businessName;

private String websiteUrl;

private String description;



private Integer ownerId;

private String ownerFullName;

private String ownerProfilePicUrl;



private Integer totalBoards;

private Integer totalPins;



private List<ShowcaseBoardDTO> showcaseBoards;



}

