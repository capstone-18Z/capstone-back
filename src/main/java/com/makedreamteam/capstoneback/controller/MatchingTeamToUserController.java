package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingTeamToUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/team-to-user")
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
        }catch (RuntimeException | IOException e){
            ResponseForm error= ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
            return ResponseEntity.ok().body(error);
        }
    }

    @PostMapping("/{matchId}/approve")
    public ResponseEntity<ResponseForm> approveRequest(@PathVariable UUID matchId,HttpServletRequest request){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.approveRequest(matchId, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException | IOException e){
            ResponseForm error= ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/{matchId}/refuse")
    public ResponseEntity<ResponseForm> refuseRequest(@PathVariable UUID matchId,HttpServletRequest request) {
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.refuseRequest(matchId, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException | IOException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }

    }
    @PostMapping("/{matchId}/delete")
    public ResponseEntity<ResponseForm> deleteRequest(@PathVariable UUID matchId,HttpServletRequest request) {
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.deleteRequest(matchId, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }

    }

    //팀이 나한테 요청한 리스트
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

    //내가 팀에게 받은 요청리스트

    @GetMapping("/allRequestFromTeam")
    public ResponseEntity<ResponseForm> getAllRequestFromTeam(HttpServletRequest request){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try{
            ResponseForm responseForm=matchingTeamToUserService.getAllRequestFromTeam(accessToken,refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm err= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(err);
        }
    }

    //팀이 신청한 리스트
    @GetMapping("/allRequestTeamToUser")
    public ResponseEntity<ResponseForm> allRequestTeamToUser(HttpServletRequest request,@RequestParam("teamId")UUID teamId){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try{
            ResponseForm responseForm=matchingTeamToUserService.getAllRequestTeamToUser(teamId,accessToken,refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm err= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(err);
        }
    }
}
