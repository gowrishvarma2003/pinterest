//package com.infy.pintrest.service;
//
//public interface PinInteractionService {
//
//}
package com.infy.pintrest.service;

import java.util.List;

import com.infy.pintrest.dto.PinDTO;

import com.infy.pintrest.entity.Pin;

public interface PinInteractionService {

    String savePin(Integer userId, Integer pinId);

    // String likePin(Integer userId,Integer pinId);

    List<Pin> getSavedPins(Integer userId);

    boolean isSaved(Integer userId, Integer pinId);

    // String removeSavedPin(Integer userId,Integer pinId);

    // sssint countSavedPins(Integer userId);

}
