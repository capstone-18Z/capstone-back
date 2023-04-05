package com.makedreamteam.capstoneback.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.dir:classpath:/static/upload}")
    private String uploadDir;

    public void uploadFile(MultipartFile file, UUID uid, Long postid) throws IOException {
        String fileName = uid + "_" + postid + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + "/" + fileName);
        Files.write(path, file.getBytes());
    }
}
