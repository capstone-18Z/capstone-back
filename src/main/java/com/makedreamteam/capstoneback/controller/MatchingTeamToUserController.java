package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MatchingTeamToUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/team-to-user")
public class MatchingTeamToUserController {
    private final MatchingTeamToUserService matchingTeamToUserService;

    public MatchingTeamToUserController(MatchingTeamToUserService matchingTeamToUserService) {
        this.matchingTeamToUserService = matchingTeamToUserService;
    }

    @PostMapping("/{teamId}/{postId}/add")
    public ResponseEntity<ResponseForm> matchRequestTeamToUser(@PathVariable UUID teamId, @PathVariable Long postId, HttpServletRequest request){
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.matchRequestTeamToUser(teamId, postId, accessToken, refreshToken);
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{waitingListId}/approve")
    public ResponseEntity<ResponseForm> approveRequestMatching(@PathVariable Long waitingListId,HttpServletRequest request){
        String accessToken= request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = matchingTeamToUserService.approveRequestTeamToUser(waitingListId, accessToken, refreshToken);

            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }
}
