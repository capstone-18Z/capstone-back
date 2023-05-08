package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingUserToTeam;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingUserToTeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/user-to-team")
public class MatchingUserToTeamController {

    private final MatchingUserToTeamService matchingUserToTeamService;
    @Autowired
    public MatchingUserToTeamController(MatchingUserToTeamService teamMemberService) {
        this.matchingUserToTeamService = teamMemberService;
    }

    @PostMapping("/{teamId}/add")
    public ResponseEntity<ResponseForm> tryMatchTeamMember(HttpServletRequest request,@RequestBody WaitingListOfMatchingUserToTeam waitingListOfMatchingUserToTeam,@PathVariable UUID teamId){
       try{
           String accessToken= request.getHeader("login-token");
           String refreshToken= request.getHeader("refresh-token");
           ResponseForm responseForm = matchingUserToTeamService.matchTry(teamId,waitingListOfMatchingUserToTeam, accessToken, refreshToken);
           return ResponseEntity.ok().body(responseForm);
       }catch (RuntimeException e){
           ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
           return ResponseEntity.internalServerError().body(error);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
    //멤버가 팀에게 매칭 신청



    //매칭 승인
    @PostMapping("/{waitingId}/approve")
    public ResponseEntity<ResponseForm> approveMatch( @PathVariable UUID waitingId, HttpServletRequest request){
        String accessToken= request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingUserToTeamService.approveMatch(waitingId,accessToken, refreshToken);
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException | IOException e){
            ResponseForm error=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{teamId}/waiting-list")
    public ResponseEntity<ResponseForm> findTeamWaitingList( @PathVariable UUID teamId, HttpServletRequest request){
        String accessToken= request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingUserToTeamService.findTeamWaitingList(teamId,accessToken, refreshToken);
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm error=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{waitingId}/fuckyou")
    public ResponseEntity<ResponseForm> fuckyouMatch(@PathVariable UUID waitingId, HttpServletRequest request){
        String accessToken= request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingUserToTeamService.fuckYouMatch(waitingId,accessToken, refreshToken);
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException | IOException e){
            ResponseForm error=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/all-my-request")
    public ResponseEntity<ResponseForm> getAllMyWaitingList(HttpServletRequest request){
        String accessToken= request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        ResponseForm allMyWaitingList = matchingUserToTeamService.getAllMyWaitingList(accessToken, refreshToken);
        return ResponseEntity.ok(allMyWaitingList);
    }


}
