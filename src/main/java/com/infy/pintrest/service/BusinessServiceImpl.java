package com.infy.pintrest.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.pintrest.dto.BusinessProfileViewDTO;
import com.infy.pintrest.dto.ShowcaseBoardDTO;
import com.infy.pintrest.entity.Board;
import com.infy.pintrest.entity.BusinessProfile;
import com.infy.pintrest.entity.User;
import com.infy.pintrest.exception.InfyPintrestException;
import com.infy.pintrest.repository.BoardRepository;
import com.infy.pintrest.repository.BusinessProfileRepository;
import com.infy.pintrest.repository.FollowRepository;
import com.infy.pintrest.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private FollowRepository followRepository;

    @Override
    public BusinessProfileViewDTO getBusinessProfileByUserId(Integer businessUserId) throws InfyPintrestException {
        // Validate user exists
        Optional<User> userOpt = userRepository.findById(businessUserId);
        if (userOpt.isEmpty()) {
            throw new InfyPintrestException("Service.USER_NOT_FOUND");
        }
        User owner = userOpt.get();

        // Find business profile
        Optional<BusinessProfile> bpOpt = businessProfileRepository.findByUserId(businessUserId);
        if (bpOpt.isEmpty()) {
            throw new InfyPintrestException("Service.BUSINESS_PROFILE_NOT_FOUND");
        }
        BusinessProfile bp = bpOpt.get();

        // All boards for owner
        List<Board> allBoards = boardRepository.findByOwnerId(businessUserId);
        int totalBoards = allBoards == null ? 0 : allBoards.size();

        // total pins (sum of pins sizes for each board)
        int totalPins = 0;
        if (allBoards != null) {
            for (Board b : allBoards) {
                if (b.getPins() != null) totalPins += b.getPins().size();
            }
        }

        // showcase boards only
        List<Board> showcaseBoards = boardRepository.findByOwnerIdAndShowcaseTrue(businessUserId);
        List<ShowcaseBoardDTO> showcaseDtos = new ArrayList<>();
        if (showcaseBoards != null) {
            for (Board sb : showcaseBoards) {
                ShowcaseBoardDTO sbdto = new ShowcaseBoardDTO();
                sbdto.setId(sb.getId());
                sbdto.setTitle(sb.getTitle());
                sbdto.setCoverImageUrl(sb.getCoverImageUrl());
                sbdto.setPinCount(sb.getPins() == null ? 0 : sb.getPins().size());
                showcaseDtos.add(sbdto);
            }
        }

        BusinessProfileViewDTO result = new BusinessProfileViewDTO();
        result.setBusinessProfileId(bp.getId());
        result.setBusinessName(bp.getBusinessName());
        result.setWebsiteUrl(bp.getWebsiteUrl());
        result.setDescription(bp.getDescription());
        result.setCategory(bp.getCategory());
        result.setVerified(bp.isVerified());
        result.setOwnerId(owner.getId());
        result.setOwnerUsername(owner.getName());
        result.setOwnerFullName(owner.getFullname() != null ? owner.getFullname() : owner.getName());
        result.setOwnerProfilePicUrl(owner.getProfilePath());
        
        // Get cover image from first showcase board or first board
        String coverImage = null;
        if (!showcaseDtos.isEmpty() && showcaseDtos.get(0).getCoverImageUrl() != null) {
            coverImage = showcaseDtos.get(0).getCoverImageUrl();
        } else if (allBoards != null && !allBoards.isEmpty() && allBoards.get(0).getCoverImageUrl() != null) {
            coverImage = allBoards.get(0).getCoverImageUrl();
        }
        result.setCoverImageUrl(coverImage);
        
        result.setTotalBoards(totalBoards);
        result.setTotalPins(totalPins);
        
        // Get follower count
        List<com.infy.pintrest.entity.Follow> followers = followRepository.findByFollowingId(owner.getId());
        result.setFollowerCount(followers == null ? 0 : followers.size());
        
        result.setShowcaseBoards(showcaseDtos);
        return result;
    }

    @Override
    public List<BusinessProfileViewDTO> getAllBusinessProfiles() {
        List<BusinessProfile> all = businessProfileRepository.findAll();
        List<BusinessProfileViewDTO> out = new ArrayList<>();
        for (BusinessProfile bp : all) {
            User owner = bp.getUser();
            if (owner == null) {
                continue;
            }
            
            // Fetch the full user to ensure all fields are loaded
            Optional<User> ownerOpt = userRepository.findById(owner.getId());
            if (ownerOpt.isEmpty()) continue;
            owner = ownerOpt.get();
            
            Integer ownerId = owner.getId();

            // compute counts
            List<Board> boards = boardRepository.findByOwnerId(ownerId);
            int totalBoards = boards == null ? 0 : boards.size();
            int totalPins = 0;
            if (boards != null) {
                for (Board b : boards) {
                    if (b.getPins() != null) totalPins += b.getPins().size();
                }
            }

            List<Board> showcaseBoards = boardRepository.findByOwnerIdAndShowcaseTrue(ownerId);
            List<ShowcaseBoardDTO> showcaseDtos = new ArrayList<>();
            if (showcaseBoards != null) {
                for (Board sb : showcaseBoards) {
                    ShowcaseBoardDTO sbdto = new ShowcaseBoardDTO();
                    sbdto.setId(sb.getId());
                    sbdto.setTitle(sb.getTitle());
                    sbdto.setCoverImageUrl(sb.getCoverImageUrl());
                    sbdto.setPinCount(sb.getPins() == null ? 0 : sb.getPins().size());
                    showcaseDtos.add(sbdto);
                }
            }

            BusinessProfileViewDTO dto = new BusinessProfileViewDTO();
            dto.setBusinessProfileId(bp.getId());
            dto.setBusinessName(bp.getBusinessName());
            dto.setWebsiteUrl(bp.getWebsiteUrl());
            dto.setDescription(bp.getDescription());
            dto.setCategory(bp.getCategory());
            dto.setVerified(bp.isVerified());
            dto.setOwnerId(ownerId);
            dto.setOwnerUsername(owner.getName());
            dto.setOwnerFullName(owner.getFullname() != null ? owner.getFullname() : owner.getName());
            dto.setOwnerProfilePicUrl(owner.getProfilePath());
            
            // Get cover image from first showcase board or first board
            String coverImage = null;
            if (!showcaseDtos.isEmpty() && showcaseDtos.get(0).getCoverImageUrl() != null) {
                coverImage = showcaseDtos.get(0).getCoverImageUrl();
            } else if (boards != null && !boards.isEmpty() && boards.get(0).getCoverImageUrl() != null) {
                coverImage = boards.get(0).getCoverImageUrl();
            }
            dto.setCoverImageUrl(coverImage);
            
            dto.setTotalBoards(totalBoards);
            dto.setTotalPins(totalPins);
            
            // Get follower count
            List<com.infy.pintrest.entity.Follow> followers = followRepository.findByFollowingId(ownerId);
            dto.setFollowerCount(followers == null ? 0 : followers.size());
            
            dto.setShowcaseBoards(showcaseDtos);
            out.add(dto);
        }
        return out;
    }
}
