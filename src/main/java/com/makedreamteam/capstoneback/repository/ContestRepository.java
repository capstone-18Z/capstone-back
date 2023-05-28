package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Contest;
import com.makedreamteam.capstoneback.domain.FileData;
import com.makedreamteam.capstoneback.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, String> {
    @Query("SELECT c FROM Contest c")
    List<Contest> getAllContest(Pageable pageable);

    List<Contest> findByTitleContainsOrderByCidDesc(String title, Pageable pageable);
    List<Contest> findByTitleContains(String title);
}
