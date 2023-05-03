package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingTeamToUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public ResponseEntity<ResponseForm> requsetMatching(@PathVariable UUID teamId, HttpServletRequest request){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.requestMatching(teamId, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
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
}
