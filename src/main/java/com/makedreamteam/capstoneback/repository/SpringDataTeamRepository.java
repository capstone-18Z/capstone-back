package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataTeamRepository extends JpaRepository<Team, Long>  {
    List<Team> findByTitleContaining(String title);
}
