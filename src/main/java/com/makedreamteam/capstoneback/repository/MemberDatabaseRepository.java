package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.MemberDatabase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDatabaseRepository extends JpaRepository<MemberDatabase, String> {
}
