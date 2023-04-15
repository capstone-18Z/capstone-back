package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamKeywordRepository extends JpaRepository<TeamKeyword, UUID> {
}
