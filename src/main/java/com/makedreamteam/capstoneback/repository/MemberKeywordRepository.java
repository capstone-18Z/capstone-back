package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, UUID> {
}
