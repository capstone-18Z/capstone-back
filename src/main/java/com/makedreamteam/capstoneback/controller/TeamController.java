package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/teams" , produces="application/json;charset=UTF-8")
public class TeamController {

    private final TeamService teamService;
    @Autowired
    public TeamController(TeamService postTeamService) {
        this.teamService = postTeamService;
    }
    @GetMapping("")
    public ResponseEntity<ResponseForm> allPost(Principal principal){
        String userName=principal!=null ? principal.getName() : "";
        try{List<Team> teams=teamService.allPosts(principal);
            ResponseForm responseForm=ResponseForm.builder().message("모든 팀을 조회합니다").state(HttpStatus.OK.value()).data(TeamData.builder().dataWithoutLogin(teams).build()).build();
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm errorResponseForm=ResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }

    }

    @GetMapping("/search/{title}")
    public ResponseEntity<ResponseForm> searchPostByTitle(@PathVariable String title){
        try{
            List<Team> byTitleContaining = teamService.findByTitleContaining(title);
            ResponseForm responseForm=ResponseForm.builder()
                    .message("").state(HttpStatus.OK.value()).data(TeamData.builder().dataWithLogin(byTitleContaining).build()).build();
            return ResponseEntity.ok().body(responseForm);
        }catch (RuntimeException e){
            ResponseForm errorResponseForm=ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message("error").build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }
    @PostMapping("/{id}")
    public ResponseEntity<ResponseForm> findById(@PathVariable UUID id){
        Optional<Team> team=teamService.findById(id);
        if(team.isPresent()){
            ResponseForm responseForm=ResponseForm.builder()
                    .data(TeamData.builder().dataWithoutLogin(team).build())
                    .message("search successfully")
                    .state(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok().body(responseForm);
        }
        else{
            ResponseForm errorResponse = ResponseForm.builder()
                    .message("Failed to search team")
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }



    @PostMapping("/new")
    public ResponseEntity<ResponseForm> addNewTeam(@RequestBody PostTeamForm postTeamForm,HttpServletRequest request){
        try{
            String authToken= request.getHeader("login-token");
            Team team = teamService.addPostTeam(postTeamForm,authToken);
            ResponseForm responseForm=ResponseForm.builder()
                    .data(TeamData.builder().dataWithLogin(team).build())
                    .message("Team created successfully")
                    .state(HttpStatus.CREATED.value())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
        }catch (RuntimeException e){
            ResponseForm errorResponse = ResponseForm.builder()
                    .message("Failed to create team: " + e.getMessage())
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }



    //팀 정보 수정
    @PostMapping("/{teamid}/update")
    public ResponseEntity<ResponseForm> updateTeamInfo(@PathVariable UUID teamid, @RequestBody PostTeamForm postTeamForm,HttpServletRequest request) {
        try{
            String authToken= request.getHeader("login-token");
            Team updateTeam = teamService.update(teamid, postTeamForm,authToken);
            ResponseForm responseForm=ResponseForm.builder()
                    .message("update team")
                    .data(TeamData.builder().dataWithLogin(updateTeam).build())
                    .state(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok().body(responseForm);
        }
        catch (RuntimeException | AuthenticationException e){
            ResponseForm errorResponseForm=ResponseForm.builder()
                    .message(e.getMessage())
                    .state(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping("/{teamId}/delete")
    public ResponseEntity<ResponseForm> deleteTeam(@PathVariable UUID teamId,HttpServletRequest request){
        try {
            String authToken= request.getHeader("login-token");
            teamService.delete(teamId,authToken);
            ResponseForm responseForm=ResponseForm.builder()
                    .message("팀을 삭제했습니다")
                    .state(HttpStatus.OK.value()).build();
            return ResponseEntity.ok().body(responseForm);
        } catch (AuthenticationException | RuntimeException e){
            ResponseForm errorResponseForm=ResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }


}
