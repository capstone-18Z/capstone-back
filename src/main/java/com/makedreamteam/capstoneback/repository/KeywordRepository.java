package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<TeamKeyword, UUID> {
    @Query("SELECT tk.value, COUNT(tk) FROM TeamKeyword tk GROUP BY tk.value")
    Map<String, Integer> findTeamKeywordCount();
}
