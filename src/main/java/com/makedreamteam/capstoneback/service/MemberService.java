package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;
    @Autowired
    private final SpringDataJpaUserLangRepository springDataJpaUserLangRepository;
    @Autowired
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;


    public PostMember PostJoin(PostMember post){
        try{
            PostMember save = postMemberRepository.save(post);
            return save;
        }catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                TransactionSystemException e) {
            throw new RuntimeException("Failed to add", e);
        }
    }
    public Member MemberJoin(Member post){
        try{
            if(checkEmailDuplicate(post.getEmail())==false && checkNicknameDuplicate(post.getNickname())==false) {
                Member save = memberRepository.save(post);
                return save;
            }
            else if (checkNicknameDuplicate(post.getNickname())){
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
            else{
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        }catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                TransactionSystemException e) {
            throw new RuntimeException("Failed to add", e);
        }
    }

    public boolean checkEmailDuplicate(String email){
        return memberRepository.existsByEmail(email);
    }
    public boolean checkNicknameDuplicate(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
}
