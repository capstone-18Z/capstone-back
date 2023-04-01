package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingTeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.UUID;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@CrossOrigin
public class MatchingTeamMemberController {

    private final MatchingTeamMemberService matchingTeamMemberService;
    @Autowired
    public MatchingTeamMemberController(MatchingTeamMemberService teamMemberService) {
        this.matchingTeamMemberService = teamMemberService;
    }

    //팀에서 멤버에게 팀원 신청
    @PostMapping("/match")
    public ResponseEntity<ResponseForm> tryMatchTeamMember(@RequestBody Map<String,String> data){
       try{
           System.out.println("요처왔다아아아아");
       }catch (RuntimeException e){
            return null;
       }
        return null;
    }
    //멤버가 팀에게 매칭 신청



    //매칭 승인
    @PostMapping("/{teamId}/match/{userId}/approve")
    public ResponseEntity<ResponseForm> approveMatchTeamMember(@PathVariable UUID teamId, @PathVariable UUID userId){
        try{
            matchingTeamMemberService.addTeamMember(teamId,userId);
            ResponseForm responseForm= ResponseForm.builder().message("매칭이 완료되었습니다.").state(HttpStatus.OK.value()).build();
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    //팀원이 나갈때,팀원 삭제
    @PostMapping("/{teamId}/match/{userId}/delete")
    public ResponseEntity<ResponseForm> deleteTeamMember(@PathVariable UUID teamId,@PathVariable UUID userId){
        try{
            matchingTeamMemberService.deleteTeamMember(teamId,userId);
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
