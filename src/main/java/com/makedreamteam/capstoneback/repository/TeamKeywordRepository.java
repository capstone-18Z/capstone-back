package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TeamKeywordRepository extends JpaRepository<TeamKeyword, Long> {
    @Query("SELECT k.value AS keyword, COUNT(k) AS count FROM TeamKeyword k GROUP BY k.value")
    List<Map<String,Integer>> countOfKeyword();
}
