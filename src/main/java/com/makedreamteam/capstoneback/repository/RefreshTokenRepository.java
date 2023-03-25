package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    boolean existsByUserId(String loginUserId);

    void deleteByUserId(String loginUserId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
