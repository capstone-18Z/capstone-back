package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TeamKeywordRepository extends JpaRepository<TeamKeyword, Long> {
    @Query("select team.teamId from TeamKeyword where field in :subject")
    List<UUID> doFilterBySubject(@Param("subject") List<String> subject);

    @Query("select team.teamId from TeamKeyword where field in :rule")
    List<UUID> doFilterByRule(@Param("rule") List<String> rule);

    @Query("select team.teamId from TeamKeyword where category in :category")
    List<UUID> doFilterByCategory(@Param("category") List<String> category);
    @Query("select team.teamId from TeamKeyword where category in :category and team.teamId in :teamsId order by team.updateDate desc ")
    List<UUID> doFilterByCategoryAndRule(@Param("category") List<String> category,@Param("teamsId") List<UUID> teams);

    @Query("SELECT t FROM Team t JOIN t.teamKeyword k WHERE t.title like %:search% AND ((k.field IN :rule and k.category IN :category) OR k.field IN :subject ) order by t.updateDate desc")
    Page<Team> findAllByFilter(@Param("category") List<String> category, @Param("subject") List<String> subject, @Param("rule") List<String> rule,@Param("search") String search, Pageable pageable);

    @Query("select team from TeamKeyword  where (field in :rule and category in :category) or field in :subject order by team.updateDate desc")
    Page<Team> findAllByFilter(@Param("category") List<String> category, @Param("subject") List<String> subject, @Param("rule") List<String> rule, Pageable pageable);


}
