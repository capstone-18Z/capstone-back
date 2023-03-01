package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.PostTeam;

import java.util.List;
import java.util.Optional;

public interface PostTeamRepository {
    PostTeam save(PostTeam postTeam);
    Optional<PostTeam> findById(Long teamid);

    List<PostTeam> findAll();

    List<PostTeam> findByTitleContaining(String title);
}
