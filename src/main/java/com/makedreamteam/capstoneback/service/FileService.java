package com.makedreamteam.capstoneback.service;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.repository.FileDataRepository;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.repository.ProfileDataRepository;
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
    private final ProfileDataRepository profileDataRepository;

    @Autowired
    private Storage storage;

    private final String bucketName = "caps-1edf8.appspot.com";

    @PersistenceContext
    private EntityManager entityManager;

    public FileData uploadFile(MultipartFile file, UUID uid, Long postid) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis() + "_" + originalFileName;

        Bucket bucket=StorageClient.getInstance().bucket("caps-1edf8.appspot.com");
        InputStream content =new ByteArrayInputStream(file.getBytes());
        Blob blob=bucket.create(fileName,content,file.getContentType());
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName()+"?alt=media";

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
        fileData.setImageURL(imageUrl);

        return fileDataRepository.save(fileData);
    }


    public ProfileData uploadProfile(MultipartFile file, UUID uid) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String fileName = System.currentTimeMillis() + "_" + originalFileName;

        Bucket bucket=StorageClient.getInstance().bucket("caps-1edf8.appspot.com");
        InputStream content =new ByteArrayInputStream(file.getBytes());
        Blob blob=bucket.create(fileName,content,file.getContentType());
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName()+"?alt=media";

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

        ProfileData profileData = new ProfileData();
        profileData.setMember(memberRepository.findById(uid).get());
        profileData.setFileName(fileName);
        profileData.setFileType(fileType);
        profileData.setOriginalName(originalFileName);
        profileData.setUploadDate(LocalDateTime.now());
        profileData.setImageURL(imageUrl);

        return profileDataRepository.save(profileData);
    }


    public List<String> uploadFile(List<MultipartFile> files) throws IOException {
        List<String> images=new ArrayList<>();
        for (MultipartFile file : files){
            Bucket bucket=StorageClient.getInstance().bucket("caps-1edf8.appspot.com");
            InputStream content = new ByteArrayInputStream(file.getBytes());
            Blob blob=bucket.create(file.getOriginalFilename(),content,file.getContentType());
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName()+"?alt=media";
            images.add(imageUrl);
        }
        return images;
    }

    public void deleteFile(FileData file) throws IOException {
        String filePath = file.getFileName();

        try {
            BlobId blobId = BlobId.of("caps-1edf8.appspot.com", filePath);
            boolean deleted = storage.delete(blobId);
            if (deleted) {



                // Bucket에서도 파일 삭제
                Bucket bucket = StorageClient.getInstance().bucket(bucketName);
                Blob blob = bucket.get(filePath);
                if (blob != null) {
                    blob.delete();


                }
            } else {
                throw new IOException("Firebase Storage의 파일 삭제에 실패하였습니다: " + filePath);
            }
        } catch (StorageException e) {


            throw new IOException("Firebase Storage의 파일 삭제에 실패하였습니다: " + filePath);
        } catch (Exception e) {


            throw new IOException("파일 삭제 중 오류가 발생했습니다.");
        }

        entityManager.remove(entityManager.contains(file) ? file : entityManager.merge(file));
        entityManager.flush();
    }
    public void deleteFile(String file){
        String[] urlArr = file.split("/"); // "/"를 기준으로 문자열 분리
        String fileName = urlArr[urlArr.length - 1].split("\\?")[0];


        BlobId blobId = BlobId.of("caps-1edf8.appspot.com", fileName);
        boolean deleted = storage.delete(blobId);
        if (deleted) {


        } else {


        }
    }
}
