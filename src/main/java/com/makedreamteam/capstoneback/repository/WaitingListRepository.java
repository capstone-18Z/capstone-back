package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.WaitingListOfMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingListOfMatching, UUID> {
    Optional<WaitingListOfMatching> findByTeamIdAndUserId(UUID teamId, UUID userId);
}
