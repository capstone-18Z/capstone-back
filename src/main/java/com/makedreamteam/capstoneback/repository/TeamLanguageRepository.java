package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamLanguageRepository extends JpaRepository<TeamLanguage, UUID> {
}
