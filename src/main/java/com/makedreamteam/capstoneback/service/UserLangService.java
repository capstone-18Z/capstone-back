package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.repository.UserLangRepository;
import jakarta.transaction.Transactional;

@Transactional
public class UserLangService {
    private final UserLangRepository userLangRepository;

    public UserLangService(UserLangRepository userLangRepository){ this.userLangRepository = userLangRepository; }
}
