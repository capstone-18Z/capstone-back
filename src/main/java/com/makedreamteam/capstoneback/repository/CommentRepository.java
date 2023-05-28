package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Comment;
import com.makedreamteam.capstoneback.domain.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, String> {
    Optional<Comment> findById(Long id);
}
