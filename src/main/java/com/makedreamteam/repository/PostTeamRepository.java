package com.makedreamteam.repository;

import com.makedreamteam.domain.PostTeam;
import com.makedreamteam.domain.Team;

import java.util.List;
import java.util.Optional;

public interface PostTeamRepository {
    PostTeam save(PostTeam postTeam);
    Optional<PostTeam> findById(Long teamid);
    List<PostTeam> findByTitle(String title);
    List<PostTeam> findAll();
}
