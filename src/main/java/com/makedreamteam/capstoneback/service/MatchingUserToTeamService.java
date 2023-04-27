package com.makedreamteam.capstoneback.service;




import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.WebSocketConfig;
import com.makedreamteam.capstoneback.domain.*;
        import com.makedreamteam.capstoneback.form.ResponseForm;
        import com.makedreamteam.capstoneback.repository.*;
        import io.jsonwebtoken.Claims;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
        import java.util.Optional;
        import java.util.UUID;

@Service
@Transactional
public class MatchingUserToTeamService {
    @Autowired
    TeamMemberRepository teamMemberRepository;
    @Autowired
    WaitingListUserToTeamRepository waitingListRepository;
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



    public MatchingUserToTeamService(TeamMemberRepository teamMemberRepository, WaitingListUserToTeamRepository waitingListRepository, SpringDataTeamRepository springDataTeamRepository, MemberRepository memberRepository){
        this.teamMemberRepository=teamMemberRepository;
        this.waitingListRepository=waitingListRepository;
        this.springDataTeamRepository=springDataTeamRepository;
        this.memberRepository=memberRepository;
    }


    //userId 중복 체크 필요
    //내가 :user 상대가 team
    public ResponseForm matchTry(UUID teamId, WaitingListOfMatchingUserToTeam waitingListOfMatchingUserToTeam, String accessToken, String refreshToken) throws IOException {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            Claims userInfo= jwtTokenProvider.getClaimsToken(accessToken);
            UUID userId=UUID.fromString((String)userInfo.get("userId"));
            Team team = springDataTeamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 팀입니다."));
            List<WaitingListOfMatchingUserToTeam> allByUserId = waitingListRepository.findAllByUserId(userId);
            for(WaitingListOfMatchingUserToTeam waiting : allByUserId){
                if(team.equals(waiting.getTeam()))
                    throw new RuntimeException("이미 신청한 팀입니다.");
            }
            teamMemberRepository.findByTeamIdAndUserId(teamId,userId)
                    .ifPresent(e->{
                        throw new RuntimeException("이미 같은팀에 속해있습니다.");
                    });
            String teamLeader=team.getTeamLeader().toString();
            WebSocketConfig.MyWebSocketHandler.sendNotificationToUser(teamLeader);
            waitingListOfMatchingUserToTeam.setUserId(userId);
            waitingListOfMatchingUserToTeam.setTeam(team);
            WaitingListOfMatchingUserToTeam savedData = waitingListRepository.save(waitingListOfMatchingUserToTeam);
            return ResponseForm.builder()
                    .data(savedData)
                    .message("매칭 신청을 완료했습니다.")
                    .build();

        }else{
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm approveMatch(UUID waitingListId,String accessToken,String refreshToken){
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            WaitingListOfMatchingUserToTeam waitingList = waitingListRepository.findById(waitingListId).orElseThrow(()->{
                throw new RuntimeException("매칭 대기 리스트가 존재하지 않습니다.");
            });
            UUID userId=waitingList.getUserId();
            int field=waitingList.getField();
            Team team = waitingList.getTeam();

            team.getRequestList().removeIf(req -> req.equals(waitingList));

            TeamMember teamMember= TeamMember.builder().teamId(team.getTeamId()).teamLeader(team.getTeamLeader()).userId(userId).build();

            //매칭이 완료외었으므로 해당 대기인원 data는 삭제한다

            //waitingListRepository.delete(waitingList);

            waitingListRepository.delete(waitingList);
            //이후, 팀멤버 테이블에 해당 사용자를 추가

            teamMemberRepository.save(teamMember);

            springDataTeamRepository.save(settingTeamMember(team));


            return ResponseForm.builder().message("사용자를 팀에 추가했습니다").build();
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
    public Team settingTeamMember(Team team){

        return team;
    }

    public ResponseForm fuckYouMatch(UUID waitingListId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            WaitingListOfMatchingUserToTeam waitingListOfMatchingUserToTeam = waitingListRepository.findById(waitingListId).orElseThrow(() -> {
                throw new RuntimeException("매칭 대기 리스트가 존재하지 않습니다.");
            });

            Team team = waitingListOfMatchingUserToTeam.getTeam();

            team.getRequestList().removeIf(req -> req.equals(waitingListOfMatchingUserToTeam));

            //매칭이 완료외었으므로 해당 대기인원 data는 삭제한다
            waitingListRepository.delete(waitingListOfMatchingUserToTeam);
            springDataTeamRepository.save(team);

            return ResponseForm.builder().message("해당 요청을 거절했습니다.").build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm findTeamWaitingList(UUID teamId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            Team team = springDataTeamRepository.findById(teamId).orElseThrow(() -> {
                throw new RuntimeException("팀이 존재하지 않습니다");
            });
            List<WaitingListOfMatchingUserToTeam> allByTeamId = team.getRequestList();
            return ResponseForm.builder().message("해당 팀의 모든 신청자리스트를 반환합니다.").data(allByTeamId).build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }
}
