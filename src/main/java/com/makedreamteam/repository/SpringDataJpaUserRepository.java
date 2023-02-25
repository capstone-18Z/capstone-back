package com.makedreamteam.repository;

import com.makedreamteam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaUserRepository extends JpaRepository<User,Long>, UserReopository {
    @Override
    Optional<User> findByName(String name);
}
