package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@CrossOrigin
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    @Autowired
    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @PostMapping("/{teamId}/match/{userId}")
    public ResponseEntity<ResponseForm> matchTeamMember(@PathVariable Long teamId, @PathVariable Long userId){
        try{
            teamMemberService.addTeamMember(teamId,userId);
            ResponseForm responseForm= ResponseForm.builder().message("매칭이 완료되었습니다.").state(HttpStatus.OK.value()).build();
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/{teamId}/match/{userId}/delete")
    public ResponseEntity<ResponseForm> deleteTeamMember(@PathVariable Long teamId,@PathVariable Long userId){
        try{
            teamMemberService.deleteTeamMember(teamId,userId);
            ResponseForm responseForm=ResponseForm.builder()
                    .state(HttpStatus.OK.value()).message("정상적으로 삭제 완료했습니다.").build();
            return ResponseEntity.ok().body(responseForm);
        }catch(RuntimeException e){
            ResponseForm errorResponseForm=ResponseForm.builder()
                    .state(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }
}
