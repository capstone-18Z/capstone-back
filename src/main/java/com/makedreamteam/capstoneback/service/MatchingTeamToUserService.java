package com.makedreamteam.capstoneback.service;


import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.WebSocketConfig;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import com.makedreamteam.capstoneback.form.MypageFormForList;
import com.makedreamteam.capstoneback.form.RequestData;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class MatchingTeamToUserService {
    @Autowired
    private final WaitingListTeamToUserRepository waitingListTeamToUserRepository;
    @Autowired
    private final TeamMemberRepository teamMemberRepository;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;

    @Autowired
    private WebSocketConfig.MyWebSocketHandler myWebSocketHandler;

    public MatchingTeamToUserService(WaitingListTeamToUserRepository waitingListTeamToUserRepository, TeamMemberRepository teamMemberRepository, JwtTokenProvider jwtTokenProvider, SpringDataTeamRepository springDataTeamRepository) {
        this.waitingListTeamToUserRepository = waitingListTeamToUserRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.springDataTeamRepository = springDataTeamRepository;
    }

    public ResponseForm requestMatching(UUID teamId, UUID user,String accessToken, String refreshToken) throws IOException {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            if(springDataTeamRepository.getWantedMember(teamId)==0) {
                throw new RuntimeException("더이상 팀원을 받을 수 없는 팀 입니다.");
            }
            WaitingListOfMatchingTeamToUser waitingListOfMatchingTeamToUser = waitingListTeamToUserRepository.findWaitingListOfMatchingTeamToUserByMemberIdAndTeamId(user, teamId).orElse(null);
            List<UUID> uuids = teamMemberRepository.getMapTeamMember(teamId);
            if(uuids!=null && uuids.contains(user)){
                throw new RuntimeException("이미 같은 팀 유저 입니다.");
            }

            if(waitingListOfMatchingTeamToUser==null){
                WaitingListOfMatchingTeamToUser request= WaitingListOfMatchingTeamToUser.builder().teamId(teamId).memberId(user).build();
                myWebSocketHandler.sendNotificationToUser(user,"팀원 신청 요청이 왔습니다.");
                return ResponseForm.builder().message("신청을 완료했습니다.").data(waitingListTeamToUserRepository.save(request)).build();
            }else
                throw new RuntimeException("이미 신청한 유저 입니다.");
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm approveRequest(UUID id,String accessToken,String refreshToken) throws IOException {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            WaitingListOfMatchingTeamToUser waitingListOfMatchingTeamToUser = waitingListTeamToUserRepository.findById(id).orElseThrow(() -> {
                throw new RuntimeException("해당 신청이 존재하지 않습니다");
            });
            if(springDataTeamRepository.getWantedMember(waitingListOfMatchingTeamToUser.getTeamId())==0){
                throw new RuntimeException("더이상 팀원을 받을 수 없는 팀입니다.");
            }

            UUID teamId=waitingListOfMatchingTeamToUser.getTeamId();
            UUID userId=waitingListOfMatchingTeamToUser.getMemberId();

            Team team=springDataTeamRepository.findById(teamId).orElseThrow(()->{
                waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);
                throw new RuntimeException("해당 팀이 존재하지 않습니다");
            });
            springDataTeamRepository.save(settingTeamMember(team));
            List<UUID> uuids = teamMemberRepository.getMapTeamMember(teamId);
            if(uuids!=null && uuids.contains(userId)){
                waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);
                throw new RuntimeException("이미 같은 팀 유저 입니다.");
            }
            TeamMember newTeamMember=TeamMember.builder().teamLeader(team.getTeamLeader()).teamId(teamId).userId(userId).build();
            TeamMember save = teamMemberRepository.save(newTeamMember);
            chatRepository.deleteAllByRoom(waitingListOfMatchingTeamToUser.getId());
            waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);

            myWebSocketHandler.sendNotificationToUser(team.getTeamLeader(),"신청이 수락되었습니다.");
            return ResponseForm.builder().data(save).message("신청을 수락했습니다").build();
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm getAllRequestToMe(String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            List<UUID> teamIds=new ArrayList<>();
            List<UUID> matchIds=new ArrayList<>();
            List<RequestData> requestDataList=new ArrayList<>();
            UUID userId=jwtTokenProvider.getUserId(accessToken);
            waitingListTeamToUserRepository.findByMemberId(userId).orElseThrow(()->{
                throw new RuntimeException("요청 받은 팀이 없습니다.");
            });
            List<Object[]> allTeamsRequestToMe = waitingListTeamToUserRepository.getAllTeamsRequestToMe(userId);
            for (Object[] entry : allTeamsRequestToMe) {
                UUID teamId = (UUID)entry[0];

                UUID matchId = (UUID)entry[1];

                if (teamId != null && matchId != null) {
                    teamIds.add(teamId);
                    matchIds.add(matchId);
                }
            }
            List<Team> allById = springDataTeamRepository.findAllById(teamIds);
            for(int i=0;i<allTeamsRequestToMe.size();i++){
                requestDataList.add(RequestData.builder().matchId(matchIds.get(i)).data(allById.get(i)).build());
            }
            return ResponseForm.builder().message("해당 유저에게 신청한 팀을 반환합니다.").data(requestDataList).build();
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm refuseRequest(UUID matchId, String accessToken, String refreshToken) throws IOException {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            WaitingListOfMatchingTeamToUser request = waitingListTeamToUserRepository.findById(matchId).orElseThrow(() -> {
                throw new RuntimeException("해당 신청이 존재하지 않습니다.");
            });
            UUID teamLeader=springDataTeamRepository.findById(request.getTeamId()).orElseThrow(()->{
                throw new RuntimeException("팀원 신청한 팀이 존재하지 않습니다.");
            }).getTeamLeader();
            chatRepository.deleteAllByRoom(request.getId());
            waitingListTeamToUserRepository.delete(request);
            myWebSocketHandler.sendNotificationToUser(teamLeader,"신청이 거절되었습니다.");
            return ResponseForm.builder().message("신청을 거절했습니다.").build();
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm getAllRequestFromTeam(String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            UUID userId=jwtTokenProvider.getUserId(accessToken);
            List<WaitingListOfMatchingTeamToUser> allByMemberId = waitingListTeamToUserRepository.findAllByMemberId(userId);
            if(allByMemberId.size()==0){
                return ResponseForm.builder().message("받은 신청이 없습니다.").build();
            } else return ResponseForm.builder().message(
                    "팀으로부터 받은 신청리스트를 반환합니다."
            ).data(allByMemberId).build();


        }else return jwtTokenProvider.checkRefreshToken(refreshToken);
    }

    public ResponseForm getAllRequestTeamToUser(UUID teamId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            List<WaitingListOfMatchingTeamToUser> allByTeamId = waitingListTeamToUserRepository.findAllByTeamId(teamId);
            List<MypageFormForList> data=new ArrayList<>();
            for(WaitingListOfMatchingTeamToUser request : allByTeamId){
                Optional<Member> memberRepositoryById = memberRepository.findById(request.getMemberId());
                if(memberRepositoryById.isPresent()) {
                    MypageFormForList mypageFormForList = new MypageFormForList();
                    mypageFormForList.setId(request.getId());
                    mypageFormForList.setInfo(memberRepositoryById.get());
                    data.add(mypageFormForList);
                }
            }
            if(allByTeamId.size()==0){
                return ResponseForm.builder().message("보낸 신청이 없습니다.").build();
            } else return ResponseForm.builder().message(
                    "보낸 신청리스트를 반환합니다."
            ).data(data).build();


        }else return jwtTokenProvider.checkRefreshToken(refreshToken);
    }

    public ResponseForm deleteRequest(UUID matchId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            waitingListTeamToUserRepository.findById(matchId).orElseThrow(()->{
                throw new RuntimeException("해당 신청이 존재하지 않습니다");
            });
            waitingListTeamToUserRepository.deleteById(matchId);
            return ResponseForm.builder().message("신청을 취소했습니다.").build();
        }else return jwtTokenProvider.checkRefreshToken(refreshToken);
    }

    public Team settingTeamMember(Team team){
        int currentMember= team.getCurrentTeamMemberCount();
        int wantedMember=team.getWantTeamMemberCount();
        if(wantedMember-1<0)
            throw new RuntimeException("더이상 팀원을 받을 수 없습니다");
        team.setCurrentTeamMemberCount((byte) (currentMember+1));
        team.setWantTeamMemberCount((byte) (wantedMember-1));
        return team;
    }
}
