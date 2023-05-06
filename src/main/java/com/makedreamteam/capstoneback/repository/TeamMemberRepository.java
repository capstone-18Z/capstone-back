package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);

    @Query("select t.teamMemberId from TeamMember t where t.teamId=:teamId")
    List<UUID> findAllByTeamId(@Param("teamId") UUID teamId);


    @Query("select userId from TeamMember where teamId = :teamId")
    List<UUID> getMapTeamMember(@Param("teamId")UUID teamId);


    @Query("select teamId from TeamMember where userId=:userId")
    List<UUID> getTeams(@Param("userId")UUID userId);
}
