package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamLang;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;


public interface TeamLangRepository {
    TeamLang save(TeamLang teamLang);
    Optional<TeamLang> findById(Long teamid);
    List<TeamLang> findAll();
}
