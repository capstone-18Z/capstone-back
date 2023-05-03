package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WaitingListTeamToUserRepository extends JpaRepository<WaitingListOfMatchingTeamToUser,Long> {

    Optional<WaitingListOfMatchingTeamToUser> findWaitingListOfMatchingTeamToUserByMemberIdAndTeamId(UUID memberId,UUID teamId);

}
