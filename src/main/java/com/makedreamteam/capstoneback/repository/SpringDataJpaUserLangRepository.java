package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.UserLang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaUserLangRepository extends JpaRepository<UserLang,Long> {
}
