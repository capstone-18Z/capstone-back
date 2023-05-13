package com.makedreamteam.capstoneback.repository;

import java.util.*;

import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(UUID id);
    Optional<Member> findByNickname(String nickname);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    @Override
    List<Member> findAll();

    @Query("SELECT m FROM Member m")
    List<Member> getAllMember(Pageable pageable);

    @Query("SELECT m FROM Member m ORDER BY RAND() LIMIT 4")
    List<Member> findRandomMembers();

    //추천
    @Query("SELECT tk.team.teamId  FROM TeamKeyword tk " +
            "WHERE EXISTS (" +
            " SELECT 1 FROM MemberKeyword mk" +
            " WHERE mk.category = tk.category and mk.field=tk.field and mk.sub=tk.sub AND mk.member.id=:userId AND mk.member.id<>tk.team.teamLeader" +
            ")")
    List<UUID> findTeamWithSameKeyword(@Param("userId") UUID userId);
    @Query("SELECT tl.team, (ml.c*tl.c + ml.cpp*tl.cpp+ml.cs*tl.cs+ml.html*tl.html+ml.java*tl.java+ml.javascript*tl.javascript+ml.kotlin*tl.kotlin+ml.python*tl.python+ml.R*tl.R+ml.sql_Lang*tl.sql_Lang+ml.swift*tl.swift+ml.typescript*tl.typescript) " +
            " FROM  MemberLang ml, TeamLanguage  tl  WHERE ml.member.id = :memberId and tl.team.teamId in :teamId order by (ml.c*tl.c + ml.cpp*tl.cpp+ml.cs*tl.cs+ml.html*tl.html+ml.java*tl.java+ml.javascript*tl.javascript+ml.kotlin*tl.kotlin+ml.python*tl.python+ml.R*tl.R+ml.sql_Lang*tl.sql_Lang+ml.swift*tl.swift+ml.typescript*tl.typescript) DESC,ml.member.id desc")
    List<Object[]> recommendTeamWithLang(@Param("teamId")List<UUID> teamId, @Param("memberId") UUID memberId, Pageable pageable);
    @Query("SELECT tf.team,(mf.android*tf.android+mf.node*tf.node+mf.react*tf.react+mf.spring*tf.spring+mf.tdmax*tf.tdmax+mf.unity*tf.unity+mf.unreal*tf.unreal+mf.xcode*tf.xcode) from MemberFramework mf,TeamFramework  tf where mf.member.id = :memberId and tf.team.teamId in :teamId order by (mf.android*tf.android+mf.node*tf.node+mf.react*tf.react+mf.spring*tf.spring+mf.tdmax*tf.tdmax+mf.unity*tf.unity+mf.unreal*tf.unreal+mf.xcode*tf.xcode) DESC ")
    List<Object[]> recommendTeamWithFramework(@Param("teamId")List<UUID> teamId, @Param("memberId") UUID memberId, Pageable pageable);
    @Query("select td.team,(md.mariadbL*td.mariadbL+md.mongodbL*td.mongodbL+md.mysqlL*td.mysqlL+md.schemaL*td.schemaL) from MemberDatabase md,TeamDatabase td WHERE md.member.id= :memberId and td.team.teamId in :teamId order by (md.mariadbL*td.mariadbL+md.mongodbL*td.mongodbL+md.mysqlL*td.mysqlL+md.schemaL*td.schemaL) desc ")
    List<Object[]> recommendTeamWithDatabase(@Param("teamId")List<UUID> teamId, @Param("memberId") UUID memberId, Pageable pageable);

    Page<Member> findMembersByNicknameContaining(String nickname, Pageable page);
}
