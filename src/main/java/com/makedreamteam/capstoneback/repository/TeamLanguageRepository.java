package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamLanguage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamLanguageRepository extends JpaRepository<TeamLanguage, Long> {

    @Query("SELECT ml.member, (ml.c*tl.c + ml.cpp*tl.cpp+ml.cs*tl.cs+ml.html*tl.html+ml.java*tl.java+ml.javascript*tl.javascript+ml.kotlin*tl.kotlin+ml.python*tl.python+ml.R*tl.R+ml.sql_Lang*tl.sql_Lang+ml.swift*tl.swift+ml.typescript*tl.typescript) " +
            " FROM  MemberLang ml, TeamLanguage  tl  WHERE tl.team = :team and ml.member.id in :memberId order by (ml.c*tl.c + ml.cpp*tl.cpp+ml.cs*tl.cs+ml.html*tl.html+ml.java*tl.java+ml.javascript*tl.javascript+ml.kotlin*tl.kotlin+ml.python*tl.python+ml.R*tl.R+ml.sql_Lang*tl.sql_Lang+ml.swift*tl.swift+ml.typescript*tl.typescript) DESC,ml.member.id desc")
    List<Object[]> recommendMemberWithLang(@Param("memberId")List<UUID> memberId, @Param("team") Team team, Pageable pageable);
}
