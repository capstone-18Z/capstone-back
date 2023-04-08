package com.makedreamteam.capstoneback.service;


import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.RefreshToken;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingUserToTeam;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.form.ResponseFormForMyPage;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.repository.RefreshTokenRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.repository.WaitingListUserToTeamRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class MyPageService {
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;
    @Autowired
    private final WaitingListUserToTeamRepository waitingListUserToTeamRepository;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    public MyPageService(SpringDataTeamRepository springDataTeamRepository, PostMemberRepository postMemberRepository, WaitingListUserToTeamRepository waitingListUserToTeamRepository, RefreshTokenRepository refreshTokenRepository) {
        this.springDataTeamRepository = springDataTeamRepository;
        this.postMemberRepository = postMemberRepository;
        this.waitingListUserToTeamRepository = waitingListUserToTeamRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public ResponseForm getMyPageInfo(String accessToken, String refreshToken) {
        //내가만든 모든 팀 리스트
        if(jwtTokenProvider.isValidAccessToken(accessToken)) {
            Claims userInfo= jwtTokenProvider.getClaimsToken(accessToken);
            UUID userId=UUID.fromString((String)userInfo.get("userId"));
            List<Team> myAllTeams = springDataTeamRepository.findByTeamLeader(userId);
            //내가 만든 팀에 들어온 요청 리스트
            //내가 작성한 유저 포스트
            List<PostMember> myAllPost = postMemberRepository.findAllByMember_Id(userId);
            //내가 팀원 신청한 리스트
            List<WaitingListOfMatchingUserToTeam> ListOfRequestISubmitted = waitingListUserToTeamRepository.findAllByUserId(userId);
            ResponseFormForMyPage responseFormForMyPage= ResponseFormForMyPage.builder().myAllTeams(myAllTeams).myAllPost(myAllPost).ListOfRequestISubmitted(ListOfRequestISubmitted).build();
            return ResponseForm.builder().message("마이페이지").data(responseFormForMyPage).build();
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
