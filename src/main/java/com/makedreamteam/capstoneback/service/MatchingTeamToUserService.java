package com.makedreamteam.capstoneback.service;


import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import com.makedreamteam.capstoneback.repository.WaitingListTeamToUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    private final SpringDataTeamRepository springDataTeamRepository;

    public MatchingTeamToUserService(WaitingListTeamToUserRepository waitingListTeamToUserRepository, TeamMemberRepository teamMemberRepository, JwtTokenProvider jwtTokenProvider, SpringDataTeamRepository springDataTeamRepository) {
        this.waitingListTeamToUserRepository = waitingListTeamToUserRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.springDataTeamRepository = springDataTeamRepository;
    }

    public ResponseForm requestMatching(UUID teamId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            UUID userId=jwtTokenProvider.getUserId(accessToken);
            WaitingListOfMatchingTeamToUser waitingListOfMatchingTeamToUser = waitingListTeamToUserRepository.findWaitingListOfMatchingTeamToUserByMemberIdAndTeamId(userId, teamId).orElse(null);
            List<UUID> uuids = teamMemberRepository.getMapTeamMember(teamId);
            if(uuids!=null && uuids.contains(userId)){
                throw new RuntimeException("이미 같은 팀 유저 입니다.");
            }

            if(waitingListOfMatchingTeamToUser==null){
                WaitingListOfMatchingTeamToUser request= WaitingListOfMatchingTeamToUser.builder().teamId(teamId).memberId(userId).build();
                return ResponseForm.builder().message("신청을 완료했습니다.").data(waitingListTeamToUserRepository.save(request)).build();
            }else
                throw new RuntimeException("이미 신청한 유저 입니다.");
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm approveRequest(Long id,String accessToken,String refreshToken){
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            WaitingListOfMatchingTeamToUser waitingListOfMatchingTeamToUser = waitingListTeamToUserRepository.findById(id).orElseThrow(() -> {
                throw new RuntimeException("해당 신청이 존재하지 않습니다");
            });

            UUID teamId=waitingListOfMatchingTeamToUser.getTeamId();
            UUID userId=waitingListOfMatchingTeamToUser.getMemberId();

            Team team=springDataTeamRepository.findById(teamId).orElseThrow(()->{
                waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);
                throw new RuntimeException("해당 팀이 존재하지 않습니다");
            });

            List<UUID> uuids = teamMemberRepository.getMapTeamMember(teamId);
            if(uuids!=null && uuids.contains(userId)){
                waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);
                throw new RuntimeException("이미 같은 팀 유저 입니다.");
            }
            TeamMember newTeamMember=TeamMember.builder().teamLeader(team.getTeamLeader()).teamId(teamId).userId(userId).build();
            TeamMember save = teamMemberRepository.save(newTeamMember);
            waitingListTeamToUserRepository.delete(waitingListOfMatchingTeamToUser);
            return ResponseForm.builder().data(save).message("신청을 수락했습니다").build();
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }
    }
}
