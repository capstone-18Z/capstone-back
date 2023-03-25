package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTeamRepository extends JpaRepository<Team, UUID>  {
    List<Team> findByTitleContaining(String title);
    List<Team> findByTeamLeader(UUID userID);
}
