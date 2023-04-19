package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Contest;
import com.makedreamteam.capstoneback.domain.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, String> {
}
