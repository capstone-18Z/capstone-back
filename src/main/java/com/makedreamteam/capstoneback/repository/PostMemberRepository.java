package com.makedreamteam.capstoneback.repository;


import com.makedreamteam.capstoneback.domain.PostMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostMemberRepository extends JpaRepository<PostMember, Long> {
    Optional<PostMember> findByPostId(Long PostId);
    List<PostMember> findAllByMember_Id(UUID memberId);

    List<PostMember> findPostMemberByTitleContaining(String title, Pageable p);
    @Query("SELECT p FROM PostMember p")
    List<PostMember> getAllPost(Pageable p);
}
