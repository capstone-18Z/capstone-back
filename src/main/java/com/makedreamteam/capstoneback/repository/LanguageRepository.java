package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LanguageRepository extends JpaRepository<Language,Long> {
}