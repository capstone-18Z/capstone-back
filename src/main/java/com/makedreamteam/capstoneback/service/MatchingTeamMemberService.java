package com.makedreamteam.capstoneback.service;



import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MatchingTeamMemberService {
    @Autowired
    TeamMemberRepository teamMemberRepository;
    @Autowired
    WaitingListRepository waitingListRepository;
    @Autowired
    SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostMemberRepository postMemberRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public MatchingTeamMemberService(TeamMemberRepository teamMemberRepository,WaitingListRepository waitingListRepository,SpringDataTeamRepository springDataTeamRepository,MemberRepository memberRepository){
        this.teamMemberRepository=teamMemberRepository;
        this.waitingListRepository=waitingListRepository;
        this.springDataTeamRepository=springDataTeamRepository;
        this.memberRepository=memberRepository;
    }


    public void matchTry(UUID teamId, UUID userId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){

        }else{

        }
    }
}
