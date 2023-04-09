package com.makedreamteam.capstoneback.service;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import com.makedreamteam.capstoneback.domain.FileData;
import com.makedreamteam.capstoneback.domain.FileType;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.repository.FileDataRepository;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final PostMemberRepository postMemberRepository;

    @Autowired
    private final FileDataRepository fileDataRepository;
    @Autowired
    private Storage storage;


    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.upload.dir:classpath:/static/upload}")
    private String uploadDir;

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    public FileData uploadFile(MultipartFile file, UUID uid, Long postid) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        Path path = Paths.get(uploadDir + "/" + fileName);
        Files.write(path, file.getBytes());

        FileType fileType;
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
                fileType = FileType.IMAGE;
                break;
            case "pdf":
                fileType = FileType.PDF;
                break;
            default:
                fileType = FileType.OTHER;
        }

        FileData fileData = new FileData();
        fileData.setMember(memberRepository.findById(uid).get());
        fileData.setPost(postMemberRepository.findByPostId(postid).get());
        fileData.setFileName(fileName);
        fileData.setFileType(fileType);
        fileData.setOriginalName(originalFileName);
        fileData.setUploadDate(LocalDateTime.now());

        return fileDataRepository.save(fileData);
    }

    public List<String> uploadFile(List<MultipartFile> files) throws IOException {
        List<String> images=new ArrayList<>();
        for (MultipartFile file : files){
            Bucket bucket=StorageClient.getInstance().bucket("caps-1edf8.appspot.com");
            InputStream content =new ByteArrayInputStream(file.getBytes());
            Blob blob=bucket.create(file.getOriginalFilename(),content,file.getContentType());
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName()+"?alt=media";
            images.add(imageUrl);
        }
        return images;
    }

    public void deleteFile(FileData file){
        String filePath = uploadDir + "/" + file.getFileName();
        File deleteFile = new File(filePath);
        if (deleteFile.exists()) {
            deleteFile.delete();
        }

        entityManager.remove(entityManager.contains(file) ? file : entityManager.merge(file));
        entityManager.flush();
    }
}
