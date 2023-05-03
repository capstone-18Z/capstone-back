package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamFramework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamFrameworkRepository extends JpaRepository<TeamFramework,Long> {
    @Query("select xcode+unrealEngine+unity+tdmax+spring+react+nodejs+androidStudio from TeamFramework  where id=:tf_id")
    int getTeamFrameworkTotalWeight(@Param("tf_id")long id);

}
