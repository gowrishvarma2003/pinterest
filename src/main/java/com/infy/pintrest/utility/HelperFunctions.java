package com.infy.pintrest.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.management.RuntimeErrorException;
import org.springframework.web.multipart.MultipartFile;

public class HelperFunctions {

    public String saveFile(MultipartFile file, String uploadDir) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            return uploadDir + "/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeErrorException(null, e.getMessage());
        }
    }

}
