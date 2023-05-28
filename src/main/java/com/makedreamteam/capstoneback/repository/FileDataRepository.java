package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileDataRepository extends JpaRepository<FileData, String> {
    Optional<FileData> findByImageURL(String imageURL);
}
