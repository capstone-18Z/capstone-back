package com.makedreamteam.repository;

import java.util.List;
import java.util.Optional;

public interface TeamLangRepository {
    TeamLangRepository save(TeamLangRepository teamLang);
    Optional<TeamLangRepository> findById(Long teamid);
    List<TeamLangRepository> findAll();
}
