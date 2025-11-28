//package com.infy.pintrest.service;
//
//public interface InvitationService {
//
//}

package com.infy.pintrest.service;



import java.util.List;



import com.infy.pintrest.dto.InvitationCreateDTO;

import com.infy.pintrest.dto.InvitationDTO;

import com.infy.pintrest.exception.InfyPintrestException;



public interface InvitationService {

InvitationDTO sendInvitation(Integer senderId,InvitationCreateDTO createDTO) throws InfyPintrestException;

List<InvitationDTO> getReceivedInvitations(Integer userId);

InvitationDTO respondInvitation(Integer invitationId,Integer receiverId,boolean accept) throws InfyPintrestException;

void cancelInvitation(Integer invitationId,Integer senderId) throws InfyPintrestException;



}
