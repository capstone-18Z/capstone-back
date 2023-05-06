package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SpringDataTeamRepository extends JpaRepository<Team, UUID>  {
    List<Team> findByTeamLeader(UUID userID);

    //team의 키워드와 member키워드가 같은 member 즉, 목적이 같은 맴버를 반환한다.
    @Query("SELECT mk.member FROM MemberKeyword mk " +
            "WHERE EXISTS (" +
            " SELECT 1 FROM TeamKeyword tk" +
            " WHERE mk.category = tk.category and mk.field=tk.field and mk.sub=tk.sub AND tk.team.teamId=:teamId AND mk.member.id<>tk.team.teamLeader" +
            ")")
    List<Member> findMemberAndTeamKeywordValues2(@Param("teamId") UUID teamId);
    @Query("SELECT ml.member, (ml.c*tl.c + ml.cpp*tl.cpp+ml.cs*tl.cs+ml.html*tl.html+ml.java*tl.java+ml.javascript*tl.javascript+ml.kotlin*tl.kotlin+ml.python*tl.python+ml.R*tl.R+ml.sql_Lang*tl.sql_Lang+ml.swift*tl.swift+ml.typescript*tl.typescript) " +
            " FROM  MemberLang ml, TeamLanguage  tl  WHERE tl.team.teamId = :teamId and ml.member.id in :memberId order by (ml.c*tl.c + ml.cpp*tl.cpp+ml.cs*tl.cs+ml.html*tl.html+ml.java*tl.java+ml.javascript*tl.javascript+ml.kotlin*tl.kotlin+ml.python*tl.python+ml.R*tl.R+ml.sql_Lang*tl.sql_Lang+ml.swift*tl.swift+ml.typescript*tl.typescript) DESC,ml.member.id desc")
    List<Object[]> recommendMemberWithLang(@Param("memberId")List<UUID> memberId, @Param("teamId") UUID teamId, Pageable pageable);
    @Query("SELECT mf.member,(mf.android*tf.androidStudio+mf.node*tf.nodejs+mf.react*tf.react+mf.spring*tf.spring+mf.tdmax*tf.tdmax+mf.unity*tf.unity+mf.unreal*tf.unrealEngine+mf.xcode*tf.xcode) from MemberFramework mf,TeamFramework  tf where tf.team.teamId = :teamId and mf.member.id in :memberId order by (mf.android*tf.androidStudio+mf.node*tf.nodejs+mf.react*tf.react+mf.spring*tf.spring+mf.tdmax*tf.tdmax+mf.unity*tf.unity+mf.unreal*tf.unrealEngine+mf.xcode*tf.xcode) DESC ")
    List<Object[]> recommendMemberWithFramework(@Param("memberId")List<UUID> memberId, @Param("teamId") UUID teamId, Pageable pageable);
    @Query("select md.member,(md.mariadbL*td.mariadbL+md.mongodbL*td.mongodbL+md.mysqlL*td.mysqlL+md.schemaL*td.schemaL) from MemberDatabase md,TeamDatabase td WHERE td.team.teamId= :teamId and md.member.id in :memberId order by (md.mariadbL*td.mariadbL+md.mongodbL*td.mongodbL+md.mysqlL*td.mysqlL+md.schemaL*td.schemaL) desc ")
    List<Object[]> recommendMemberWithDatabase(@Param("memberId")List<UUID> memberId, @Param("teamId") UUID teamId, Pageable pageable);

    @Query("SELECT t FROM Team t ORDER BY t.updateDate DESC")
    List<Team> getAllTeamOrderByUpdateDesc(Pageable pageable);

    @Query("select count(t) from Team t")
    long getCountOfTeams();

    @Query("select count(t) from Team t where t.title in :title")
    long getCountOfTeamsByTitle(@Param("title") String title);

    @Query("select 1 from TeamMember tm where  tm.teamId = :teamId and tm.userId in :userIds")
    boolean isTeamMember(@Param("userIds") List<UUID> userIds,@Param("teamId")UUID teamId);

    Page<Team> findTeamsByTitleContainingOrderByUpdateDateDesc(String title, Pageable pageable);
    List<Team> findTeamsByTitleContaining(String title);


    List<Team> findTeamsByTeamLeader(UUID userId);

    @Query("select wantTeamMemberCount from Team where teamId = :teamId ")
    int getWantedMember(@Param("teamId")UUID teamId);


}
