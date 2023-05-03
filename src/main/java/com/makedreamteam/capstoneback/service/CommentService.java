package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.Comment;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.repository.CommentRepository;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final PostMemberRepository postMemberRepository;

    public Comment uploadComment(String comment, UUID uid, Long postid){
        Member member = memberRepository.findById(uid).get();
        Comment uploadComment = new Comment();
        uploadComment.setContent(comment);
        uploadComment.setPost(postMemberRepository.findByPostId(postid).get());
        uploadComment.setMember(member);
        uploadComment.setUploadDate(LocalDateTime.now());
        commentRepository.save(uploadComment);

        uploadComment.setParentid(uploadComment.getId());
        return commentRepository.save(uploadComment);
    }

    public Comment uploadRecomment(String comment, UUID uid, Long postid, Long commentid){
        Member member = memberRepository.findById(uid).get();
        Comment originalComment = commentRepository.findById(commentid).get();
        Comment uploadComment = new Comment();
        uploadComment.setContent(comment);
        uploadComment.setPost(postMemberRepository.findByPostId(postid).get());
        uploadComment.setMember(member);
        uploadComment.setUploadDate(LocalDateTime.now());
        uploadComment.setParentid(commentid);
        originalComment.setChildrenCount(originalComment.getChildrenCount()+1);
        commentRepository.save(originalComment);
        return commentRepository.save(uploadComment);
    }

}
