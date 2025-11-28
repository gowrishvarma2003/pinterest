//package com.infy.pintrest.dto;
//
//public class PinViewDTO {
//
//}


package com.infy.pintrest.dto;

import java.time.LocalDateTime;
import com.infy.pinterest.enums.PinStatus;
import lombok.Data;

@Data
public class PinViewDTO {
    private Integer id;
    private String title;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String sourceUrl;
    private String attribution;
    private String keywords;
    private String topics;
    private String productTags;
    private boolean isPrivate;
    private PinStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer boardId;
    private String boardTitle;
    private Integer userId;
    
    // User details for displaying pin owner
    private String userName;
    private String userFullname;
    private String userProfilePath;
}
