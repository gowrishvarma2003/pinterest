//package com.infy.pintrest.service;
//
//public interface PinLikeService {
//
//}

package com.infy.pintrest.service;

import java.util.List;

import com.infy.pintrest.entity.Pin;

public interface PinLikeService {

    public String likePin(Integer userId, Integer pinId);

    public int getLikes(Integer pinId);

    List<Pin> getLikedPins(Integer userId);

}