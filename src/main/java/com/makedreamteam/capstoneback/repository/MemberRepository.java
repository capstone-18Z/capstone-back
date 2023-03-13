package com.makedreamteam.capstoneback.repository;

import java.util.Optional;
import com.makedreamteam.capstoneback.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
}
