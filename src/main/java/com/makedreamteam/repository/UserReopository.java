package com.makedreamteam.repository;

import com.makedreamteam.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserReopository {
    User save(User member);
    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
    List<User> findAll();
}
