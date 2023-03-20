package com.makedreamteam.capstoneback.service;



import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.domain.WaitingList;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import com.makedreamteam.capstoneback.repository.WaitingListRepository;
import org.apache.catalina.User;
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
    public MatchingTeamMemberService(TeamMemberRepository teamMemberRepository,WaitingListRepository waitingListRepository,SpringDataTeamRepository springDataTeamRepository,MemberRepository memberRepository){
        this.teamMemberRepository=teamMemberRepository;
        this.waitingListRepository=waitingListRepository;
        this.springDataTeamRepository=springDataTeamRepository;
        this.memberRepository=memberRepository;
    }

    public void addTeamMember(UUID teamId, UUID userId){

        Optional<TeamMember> byTeamIdAndUserId = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        if(byTeamIdAndUserId.isPresent()) {
            throw new RuntimeException("이미 같은 팀에 들어가있습니다.");
        }
        else{
            TeamMember teamMember=TeamMember.builder().teamId(teamId).userId(userId).build();
            teamMemberRepository.save(teamMember);
        }

    }
    public void deleteTeamMember(UUID teamId, UUID userId){
        Optional<TeamMember> byTeamIdAndUserId = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        if(byTeamIdAndUserId.isPresent()){
            teamMemberRepository.delete(byTeamIdAndUserId.get());
        }
        else{
            throw new RuntimeException("이미 삭제되었거나 존재하지 않습니다.");
        }

    }

    public void addWaitingList(UUID teamId, UUID userId) {
        Optional<Team> team=springDataTeamRepository.findById(teamId);
        Optional<Member> user=memberRepository.findById(userId);
        Optional<WaitingList> waitingList=waitingListRepository.findByTeamIdAndUserId(teamId,userId);
        if(team.isEmpty()){
            throw new RuntimeException("팀이 삭제되었거나, 올바르지 않습니다. (teamID : "+teamId+" )");
        }
        if(user.isEmpty()){
            throw new RuntimeException("사용자가 삭제되었거나, 올바르지 않습니다. (userId : "+userId+" )");
        }

    }
}
