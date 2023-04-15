package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamFramework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamFrameworkRepository extends JpaRepository<TeamFramework,Long> {
}
