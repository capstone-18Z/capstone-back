package com.makedreamteam.capstoneback.repository;


import com.makedreamteam.capstoneback.domain.UserLang;

import java.util.Optional;

public interface UserLangRepository {

    Optional<UserLang> findById(Long id);
}
