package com.makedreamteam.repository;

import com.makedreamteam.domain.Team;
import com.makedreamteam.domain.User;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(Long teamid);
    List<Team> findAll();
}
