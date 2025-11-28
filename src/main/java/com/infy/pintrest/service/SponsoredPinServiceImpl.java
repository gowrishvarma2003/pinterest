package com.infy.pintrest.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.infy.pintrest.dto.SponsoredPinDTO;
import com.infy.pintrest.entity.SponsoredPin;
import com.infy.pintrest.repository.SponsoredPinRepository;

@Service
@Transactional
public class SponsoredPinServiceImpl implements SponsoredPinService {

    @Autowired
    private SponsoredPinRepository repo;

    ModelMapper mapper = new ModelMapper();

    @Override
    public SponsoredPinDTO createSponsoredPin(SponsoredPinDTO dto, MultipartFile file) {
        SponsoredPin sp = mapper.map(dto, SponsoredPin.class);

        if (file != null && !file.isEmpty()) {
            String path = saveAdImage(file);
            sp.setImageUrl(path);
        }

        SponsoredPin saved = repo.save(sp);
        return mapper.map(saved, SponsoredPinDTO.class);
    }

    @Override
    public List<SponsoredPinDTO> getActiveSponsoredPins() {
        return repo.findByActiveTrue()
                .stream()
                .map(sp -> mapper.map(sp, SponsoredPinDTO.class))
                .collect(Collectors.toList());
    }

    private String saveAdImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get("ads-uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String ext = "";
            if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
                ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }

            String name = UUID.randomUUID() + ext;
            Path filepath = uploadPath.resolve(name);
            file.transferTo(filepath.toFile());
            return "ads-uploads/" + name;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
