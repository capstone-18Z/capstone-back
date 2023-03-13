package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.PostTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataJpaRPostTeamRepository extends JpaRepository<PostTeam,Long>,PostTeamRepository {
    List<PostTeam> findByTitle(String title);
}
