package com.makedreamteam.capstoneback.repository;


import com.makedreamteam.capstoneback.domain.PostMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostMemberRepository extends JpaRepository<PostMember, String> {
    Optional<PostMember> findByPostId(Long PostId);
}
