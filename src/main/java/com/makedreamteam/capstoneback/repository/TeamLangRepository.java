package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamLang;

import java.util.List;
import java.util.Optional;

public interface TeamLangRepository {
    TeamLang save(TeamLang teamLang);
    Optional<TeamLang> findById(Long teamid);
    List<TeamLang> findAll();
}
