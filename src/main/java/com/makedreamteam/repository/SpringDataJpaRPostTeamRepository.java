package com.makedreamteam.repository;

import com.makedreamteam.domain.PostTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaRPostTeamRepository extends JpaRepository<PostTeam,Long>,PostTeamRepository {
    List<PostTeam> findByTitle(String title);
}
