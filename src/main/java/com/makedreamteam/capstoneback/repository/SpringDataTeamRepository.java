package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTeamRepository extends JpaRepository<Team, UUID>  {
    List<Team> findByTitleContaining(String title);
    List<Team> findByTeamLeader(UUID userID);
    List<Team> findAllByOrderByUpdateDateDesc();


    @Query("select mD from Team t ,MemberKeyword mk,MemberDB mD, MemberFramework mF where t.teamKeywords in mk.")
    List<Member> recommendMember(Member member);

}
