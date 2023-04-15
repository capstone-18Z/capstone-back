package com.makedreamteam.capstoneback.service;



import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MatchingTeamToUserService {
    @Autowired
    private final WaitingListTeamToUserRepository waitingListTeamToUserRepository;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;



    public MatchingTeamToUserService(WaitingListTeamToUserRepository waitingListTeamToUserRepository, RefreshTokenRepository refreshTokenRepository, PostMemberRepository postMemberRepository, SpringDataTeamRepository springDataTeamRepository, JwtTokenProvider jwtTokenProvider) {
        this.waitingListTeamToUserRepository = waitingListTeamToUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.postMemberRepository = postMemberRepository;
        this.springDataTeamRepository = springDataTeamRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseForm matchRequestTeamToUser(UUID teamId, Long postId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            PostMember postMember = postMemberRepository.findByPostId(postId).orElseThrow(() -> {
                throw new RuntimeException("해당 포스트가 존재하지 않습니다.");
            });
            Team team=springDataTeamRepository.findById(teamId).orElseThrow(()->{
                throw new RuntimeException("팀이 존재하지 않습니다");
            });
            WaitingListOfMatchingTeamToUser waitingListOfMatchingTeamToUser= WaitingListOfMatchingTeamToUser.builder().teamId(teamId).memberPostId(postId).build();
            WaitingListOfMatchingTeamToUser save = waitingListTeamToUserRepository.save(waitingListOfMatchingTeamToUser);
            return ResponseForm.builder().message("팀원 신청을 완료했습니다").data(save).build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }
    public ResponseForm approveRequestTeamToUser(Long waitingId,String accessToken,String refreshToken){
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            WaitingListOfMatchingTeamToUser waitingListOfMatchingTeamToUser = waitingListTeamToUserRepository.findById(waitingId).orElseThrow(() -> {
                throw new RuntimeException("해당 신청이 존재하지 않습니다.");
            });
            Team team=springDataTeamRepository.findById(waitingListOfMatchingTeamToUser.getTeamId()).orElseThrow(()->{
                throw new RuntimeException("팀이 존재하지 않습니다");
            });
            Team updatedTeam = settingTeamMember(team);
            waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);
            springDataTeamRepository.save(updatedTeam);
            return ResponseForm.builder().message("정상적으로 팀원을 추가했습니다.").build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }

    private Team settingTeamMember(Team team) {
        return team;
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