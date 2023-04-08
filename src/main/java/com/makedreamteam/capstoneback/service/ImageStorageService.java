package com.makedreamteam.capstoneback.service;

import com.google.api.client.util.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageStorageService {
    @Value("${app.upload.dir:classpath:/static/upload}")
    private String uploadDir;


    public String store(MultipartFile file) throws IOException {
        Path imageDirectory = Paths.get(uploadDir);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(fileName);
        String storedFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Files.createDirectories(imageDirectory);
        Path targetPath = imageDirectory.resolve(storedFileName);
        file.transferTo(targetPath.toFile());

        return storedFileName;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
    }
    public List<InputStreamResource> getImage(List<String> images) throws IOException {
        List<InputStreamResource> imageList = new ArrayList<>();
        for(String image : images){
            ClassPathResource imgFile = new ClassPathResource("classpath:images/" + image);
            InputStream is = imgFile.getInputStream();
            imageList.add(new InputStreamResource(is));
        }
        return imageList;
    }
}