package com.makedreamteam.repository;

import com.makedreamteam.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeamRepository extends JpaRepository<Team, Long> ,TeamRepository {

}
