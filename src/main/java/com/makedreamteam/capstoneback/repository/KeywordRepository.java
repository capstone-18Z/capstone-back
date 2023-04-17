package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Keyword;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword,Long> {
    @Query("select k.member from Keyword k where k.value = :keyword and k.team is null and k.member.id <> :memberId")
   List<Member> recommendMembers(@Param("keyword") String keyword,@Param("memberId")UUID memberId);
    @Query("select k.team from Keyword k where k.member is null and k.team.teamId=:teamId")
    Team getTeam(@Param("teamId")UUID teamId);
}
