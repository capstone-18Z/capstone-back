package com.makedreamteam.capstoneback.service;



import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TeamMemberService {
    @Autowired
    TeamMemberRepository teamMemberRepository;
    public TeamMemberService(TeamMemberRepository teamMemberRepository){
        this.teamMemberRepository=teamMemberRepository;
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
}
