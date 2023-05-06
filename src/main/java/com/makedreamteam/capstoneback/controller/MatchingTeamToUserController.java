package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingTeamToUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Controller
@CrossOrigin
public class MatchingTeamToUserController {

    @Autowired
    private final MatchingTeamToUserService matchingTeamToUserService;

    public MatchingTeamToUserController(MatchingTeamToUserService matchingTeamToUserService) {
        this.matchingTeamToUserService = matchingTeamToUserService;
    }


    @PostMapping("/{teamId}/match-request")
    public ResponseEntity<ResponseForm> requsetMatching(@PathVariable UUID teamId, HttpServletRequest request, @RequestBody Map<String,String> userId){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        UUID user=UUID.fromString(userId.get("userId"));
        try {
            ResponseForm responseForm = matchingTeamToUserService.requestMatching(teamId, user,accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
            return ResponseEntity.ok().body(error);
        }
    }

    @PostMapping("/{matchId}/approve")
    public ResponseEntity<ResponseForm> approveRequest(@PathVariable Long matchId,HttpServletRequest request){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.approveRequest(matchId, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/{matchId}/refuse")
    public ResponseEntity<ResponseForm> refuseRequest(@PathVariable Long matchId,HttpServletRequest request) {
        return null;
    }
    @GetMapping("/request-TeamToUser")
    public ResponseEntity<ResponseForm> getAllRequestToMe(HttpServletRequest request){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try{
            ResponseForm responseForm=matchingTeamToUserService.getAllRequestToMe(accessToken,refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm err= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(err);
        }
    }
}
