//package com.infy.pintrest.service;
//
//public interface FollowUser {
//
//}

package com.infy.pintrest.service;



import java.util.List;



import com.infy.pintrest.dto.UserSummaryDTO;

import com.infy.pintrest.exception.InfyPintrestException;



public interface FollowUser {

String followUser(Integer followerId, Integer followingId) throws InfyPintrestException;

String unfollowUser(Integer followerId, Integer followingId) throws InfyPintrestException;

List<UserSummaryDTO> getFollowers(Integer userId) throws InfyPintrestException;

List<UserSummaryDTO> getFollowing(Integer userId) throws InfyPintrestException;

boolean isFollowing(Integer followerId, Integer targetid) throws InfyPintrestException;

}