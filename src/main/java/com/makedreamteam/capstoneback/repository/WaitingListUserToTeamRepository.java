package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingUserToTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitingListUserToTeamRepository extends JpaRepository<WaitingListOfMatchingUserToTeam, UUID> {

    List<WaitingListOfMatchingUserToTeam> findAllByUserId(UUID userId);


    @Query("select waitingId from WaitingListOfMatchingUserToTeam  where userId = :userId")
    List<UUID> findIdByUserId(@Param("userId") UUID userId);



}
