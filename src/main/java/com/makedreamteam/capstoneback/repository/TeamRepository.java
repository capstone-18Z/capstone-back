package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(Long teamid);
    List<Team> findAll();
}
