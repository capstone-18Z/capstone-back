package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WaitingListTeamToUserRepository extends JpaRepository<WaitingListOfMatchingTeamToUser,Long> {

    Optional<WaitingListOfMatchingTeamToUser> findWaitingListOfMatchingTeamToUserByMemberIdAndTeamId(UUID memberId,UUID teamId);

    @Query("select teamId,Id  from WaitingListOfMatchingTeamToUser where memberId = :memberId")
    List<Object[]> getAllTeamsRequestToMe(@Param("memberId") UUID memberId);


    Optional<WaitingListOfMatchingTeamToUser> findByMemberId(UUID memberId);
}
