package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.FileData;
import com.makedreamteam.capstoneback.domain.ProfileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileDataRepository extends JpaRepository<ProfileData, String> {
    Optional<FileData> findByImageURL(String imageURL);
}