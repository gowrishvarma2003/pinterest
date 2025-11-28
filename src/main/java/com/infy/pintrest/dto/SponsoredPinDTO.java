//package com.infy.pintrest.dto;
//
//public class SponsoredPinDTO {
//
//}

package com.infy.pintrest.dto;



import java.time.LocalDateTime;



import lombok.Data;



@Data

public class SponsoredPinDTO {

// private Integer id;

// private Long businessProfileId;

// private Long pinId;

// private String cmapaignTitle;

// private Double budget;

// private String startDate;

// private String endDate;



private Long id;

private String title;

private String description;

private String imageUrl;

private String targetUrl;

private String advertiseName;

private String keywords;

private Double budget;

private boolean active ;





}