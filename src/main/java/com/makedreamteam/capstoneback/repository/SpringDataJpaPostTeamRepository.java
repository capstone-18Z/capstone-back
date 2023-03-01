package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.PostTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaPostTeamRepository extends JpaRepository<PostTeam,Long> {

    List<PostTeam> findByTitleContaining(String title);

}
