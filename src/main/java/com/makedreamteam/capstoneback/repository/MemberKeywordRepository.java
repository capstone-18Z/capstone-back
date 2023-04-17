package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
@Repository
public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, UUID> {


    /*@Query("SELECT mk.member from MemberKeyword mk where mk.value=:teamKey and mk.member.id <> :userId")
    List<Member> findSameKeywordToTeamKeyword(@Param("teamKey")String keyword,@Param("userId") UUID teamLeader);*/
}
