package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingTeamMemberService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PostMapping("/{teamId}/{userId}/match-try")
    public ResponseEntity<ResponseForm> tryMatchTeamMember(HttpServletRequest request,@PathVariable UUID teamId, @PathVariable UUID userId){
       try{
           String accessToken= request.getHeader("login-token");
           String refreshToken= request.getHeader("refresh-token");
           matchingTeamMemberService.matchTry(teamId,userId,accessToken,refreshToken);
       }catch (RuntimeException e){
            return null;
       }
        return null;
    }
    //멤버가 팀에게 매칭 신청



    //매칭 승인
    @PostMapping("/{teamId}/match/{userId}/approve")
    public ResponseEntity<ResponseForm> approveMatchTeamMember(@PathVariable UUID teamId, @PathVariable UUID userId){
//        try{
//            matchingTeamMemberService.addTeamMember(teamId,userId);
//            ResponseForm responseForm= ResponseForm.builder().message("매칭이 완료되었습니다.").state(HttpStatus.OK.value()).build();
//            return ResponseEntity.ok().body(responseForm);
//        }catch (RuntimeException e){
//            ResponseForm error= ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
//            return ResponseEntity.badRequest().body(error);
//        }
        return null;
    }

    //팀원이 나갈때,팀원 삭제
    @PostMapping("/{teamId}/match/{userId}/delete")
    public ResponseEntity<ResponseForm> deleteTeamMember(@PathVariable UUID teamId,@PathVariable UUID userId){
//
        return null;
    }

}
