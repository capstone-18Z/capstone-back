package com.makedreamteam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaTeamLangRepository extends JpaRepository<TeamLangRepository,Long>, TeamLangRepository {
}
