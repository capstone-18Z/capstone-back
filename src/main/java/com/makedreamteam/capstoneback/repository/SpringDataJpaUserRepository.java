package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaUserRepository extends JpaRepository<User,Long>, UserRepository {
    @Override
    Optional<User> findByName(String name);
}
