package com.makedreamteam.capstoneback.service;



import com.makedreamteam.capstoneback.domain.*;
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

    public MatchingTeamMemberService(TeamMemberRepository teamMemberRepository,WaitingListRepository waitingListRepository,SpringDataTeamRepository springDataTeamRepository,MemberRepository memberRepository){
        this.teamMemberRepository=teamMemberRepository;
        this.waitingListRepository=waitingListRepository;
        this.springDataTeamRepository=springDataTeamRepository;
        this.memberRepository=memberRepository;
    }

    public void addTeamMember(UUID teamId, UUID userId){
        Optional<PostMember> postMember=postMemberRepository.findByUserId(userId);
        Optional<TeamMember> byTeamIdAndUserId = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        Optional<Team> team=springDataTeamRepository.findById(teamId);
        if(postMember.isEmpty()){
            throw new RuntimeException("postMember is empty");
        }
        if(team.isEmpty()){
            throw new RuntimeException("team is empty");
        }
        if(byTeamIdAndUserId.isPresent()) {
            throw new RuntimeException("이미 같은 팀에 들어가있습니다.");
        }
        else{
            int userField=postMember.get().getField();
            Team updatedTeam = updateMemberOfTeam(team, userField);
            TeamMember teamMember=TeamMember.builder().teamId(teamId).userId(userId).build();
            teamMemberRepository.save(teamMember);
            springDataTeamRepository.save(updatedTeam);
        }

    }

    private Team updateMemberOfTeam(Optional<Team> team,int field) {
        Team update=team.get();
        int wantedFrontMember=update.getWantedFrontMember();
        int wantedBackEndMember= update.getWantedBackEndMember();
        int wantedBasicMember = update.getWantedBasicMember();
        int currentBackMember = update.getCurrentBackMember();
        int currentBasicMember = update.getCurrentBasicMember();
        int currentFrontMember = update.getCurrentFrontMember();


        if(field==1){
            if(wantedFrontMember<currentFrontMember)
                throw new RuntimeException("더이상 front멤버를 추가할수없습니다.");
            update.setCurrentFrontMember(currentFrontMember+1);
        }else if(field==2) {
            if(wantedBackEndMember<currentBackMember)
                throw new RuntimeException("더이상 backend멤버를 추가할수없습니다.");
            update.setCurrentBackMember(currentBackMember+1);
        }else{
            if(wantedBasicMember<currentBasicMember)
                throw new RuntimeException("더이상 basic멤버를 추가할수없습니다.");
            update.setWantedBasicMember(wantedBasicMember+1);
        }
        return update;
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
        Optional<WaitingListOfMatching> waitingList=waitingListRepository.findByTeamIdAndUserId(teamId,userId);
        if(team.isEmpty()){
            throw new RuntimeException("팀이 삭제되었거나, 올바르지 않습니다. (teamID : "+teamId+" )");
        }
        if(user.isEmpty()){
            throw new RuntimeException("사용자가 삭제되었거나, 올바르지 않습니다. (userId : "+userId+" )");
        }

    }
}
