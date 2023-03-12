package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long> {
}
