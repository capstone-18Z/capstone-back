package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TeamDatabaseRepository extends JpaRepository<TeamDatabase,Long> {

    @Query("select (schemaL+mysqlL+mongodbL+mariadbL) from TeamDatabase where id =:id")
    int getTeamDatabaseTotalWeight(@Param("id") long id);
}
