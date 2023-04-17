package com.makedreamteam.capstoneback.repository;

import java.util.*;

import com.makedreamteam.capstoneback.domain.Member;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(UUID id);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    @Override
    List<Member> findAll();

}
