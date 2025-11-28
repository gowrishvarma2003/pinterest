//package com.infy.pintrest.dto;
//
//public class InvitationDTO {
//
//}

package com.infy.pintrest.dto;



import com.infy.pinterest.enums.InvitationStatus;



import lombok.Data;



@Data
public class InvitationDTO {

private Integer id;

private Integer senderId;

private Integer receiverId;

private Integer boardId;

private InvitationStatus status;

private String senderName;

private String senderFullName;

private String senderEmail;

private String senderAvatar;

private String receiverName;

private String receiverEmail;

private String receiverAvatar;

private String boardTitle;

private String boardCoverUrl;

private String sentAt;

}
