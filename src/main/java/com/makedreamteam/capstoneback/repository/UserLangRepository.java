package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.domain.UserLang;
import java.util.Optional;

public interface UserLangRepository {
    UserLang save(UserLang userLang);
    Optional<UserLang> findById(Long id);
}
