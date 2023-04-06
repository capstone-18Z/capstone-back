package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitingListTeamToUserRepository extends JpaRepository<WaitingListOfMatchingTeamToUser,Long> {

}
