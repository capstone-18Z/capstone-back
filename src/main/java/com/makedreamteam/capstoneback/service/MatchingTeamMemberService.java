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
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    public MatchingTeamMemberService(TeamMemberRepository teamMemberRepository,WaitingListRepository waitingListRepository,SpringDataTeamRepository springDataTeamRepository,MemberRepository memberRepository){
        this.teamMemberRepository=teamMemberRepository;
        this.waitingListRepository=waitingListRepository;
        this.springDataTeamRepository=springDataTeamRepository;
        this.memberRepository=memberRepository;
    }


    public ResponseForm matchTry(UUID teamId, UUID userId,Long postId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            Optional<Team> teamById = springDataTeamRepository.findById(teamId);
            Optional<Member> memberById = memberRepository.findById(userId);
            Optional<PostMember> postByPostId = postMemberRepository.findByPostId(postId);
            if(teamById.isEmpty()){
                throw new RuntimeException("team을 찾을 수 없습니다.");
            }
            if(memberById.isEmpty()){
                throw new RuntimeException("member를 찾을 수 없습니다.");
            }
            if (postByPostId.isEmpty()){
                throw new RuntimeException("post_member를 찾을 수 없습니다.");
            }

            WaitingListOfMatching waitingData=WaitingListOfMatching.builder().postId(postId).userId(userId).teamId(teamId).build();
            WaitingListOfMatching save = waitingListRepository.save(waitingData);
            return ResponseForm.builder().data(save).message("매칭 신청을 완료했습니다").build();

        }else{
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm checkRefreshToken(String refreshToken){
        if(refreshToken==null){
            throw new NullPointerException("refreshTokenRepository.findById(team.getTeamLeader()) is empty");
        }
        if(jwtTokenProvider.isValidRefreshToken(refreshToken)){//refreshtoken이 유효하다면
            //db에서 refreshtoken 검사
            Optional<RefreshToken> byRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
            if(byRefreshToken.isPresent()){//db에 refresh토큰이 존재한다면
                //access토큰 재발급 요청
                return ResponseForm.builder().message("LonginToken 재발급이 필요합니다.").build();
            }
            //존재 하지않는다면
            return ResponseForm.builder().message("허용되지 않은 refreshtoken 입니다").build();
        }
        else{//refreshtoken이  만료되었다면
            return ResponseForm.builder().message("RefreshToken 이 만료되었습니다, 다시 로그인 해주세요").build();
        }
    }
}
