package com.makedreamteam.capstoneback.repository;


import com.makedreamteam.capstoneback.domain.PostMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostMemberRepository extends JpaRepository<PostMember, Long> {
    Optional<PostMember> findByPostId(Long PostId);
    List<PostMember> findAllByMember_Id(UUID memberId);

}
