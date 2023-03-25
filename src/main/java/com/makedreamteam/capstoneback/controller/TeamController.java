package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.RefreshToken;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.exception.NotTeamLeaderException;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.form.ServiceReturn;
import com.makedreamteam.capstoneback.form.TeamData;
import com.makedreamteam.capstoneback.form.checkTokenResponsForm;
import com.makedreamteam.capstoneback.service.TeamService;
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
@RequestMapping(value = "/teams", produces = "application/json;charset=UTF-8")
public class TeamController {

    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public TeamController(TeamService postTeamService, JwtTokenProvider jwtTokenProvider) {
        this.teamService = postTeamService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("")
    public ResponseEntity<ResponseForm> allPost(HttpServletRequest request) {
        //check login
        try {
            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            List<Team> recommendTeams = null;
            List<Team> teams = teamService.allPosts(authToken,refreshToken);

            ResponseForm responseForm = ResponseForm.builder().message("모든 팀을 조회합니다").state(HttpStatus.OK.value()).data(TeamData.builder().recommendList(recommendTeams).allTeamList(teams).build()).build();
            return ResponseEntity.ok().body(responseForm);
        } catch (RuntimeException e) {
            ResponseForm errorResponseForm = ResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }

    }

    @GetMapping("/search/{title}")
    public ResponseEntity<ResponseForm> searchPostByTitle(@PathVariable String title,HttpServletRequest request) {
        try {
            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            List<Team> byTitleContaining = teamService.findByTitleContaining(title);
            ResponseForm responseForm = ResponseForm.builder()
                    .message("").state(HttpStatus.OK.value()).data(TeamData.builder().allTeamList(byTitleContaining).build()).build();
            return ResponseEntity.ok().body(responseForm);
        } catch (RuntimeException e) {
            ResponseForm errorResponseForm = ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message("error").build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseForm> findById(@PathVariable UUID id, HttpServletRequest request) {
        Team team=null;
        String newToken = null;
        try {
            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            ServiceReturn byId = teamService.findById(id, authToken, refreshToken);
            team=byId.getData();
            newToken= byId.getNewToken();
            List<PostMember> members = teamService.recommendUsers(id, 5, newToken,refreshToken);

            if (byId.getData()!=null && members != null) {
                ResponseForm responseForm = ResponseForm.builder()
                        .message("search successfully")
                        .state(HttpStatus.OK.value()).updatable(true)
                        .data(TeamData.builder().recommendList(members).team(team).build())
                        .newToken(newToken)
                        .build();
                return ResponseEntity.ok().body(responseForm);
            } else {
                ResponseForm errorResponse = ResponseForm.builder()
                        .message("Failed to search team")
                        .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (NotTeamLeaderException e) {
            ResponseForm responseForm = ResponseForm.builder()
                    .message("search successfully")
                    .state(HttpStatus.OK.value())
                    .updatable(false)
                    .data(TeamData.builder().team(team).build())
                    .newToken(newToken)
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (RuntimeException e) {
            ResponseForm errorResponse = ResponseForm.builder()
                    .message(e.getMessage())
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/new")
    public ResponseEntity<ResponseForm> addNewTeam(@RequestBody Team form, HttpServletRequest request) {
        try {

            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            ServiceReturn team =  teamService.addPostTeam(form, authToken,refreshToken);
            ResponseForm responseForm = ResponseForm.builder()
                    .data(TeamData.builder().team(team.getData()).build())
                    .message("Team created successfully")
                    .state(HttpStatus.CREATED.value())
                    .newToken(team.getNewToken())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
        } catch (RuntimeException e) {
            ResponseForm errorResponse = ResponseForm.builder()
                    .message("Failed to create team: " + e.getMessage())
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    //팀 정보 수정
    @PostMapping("/{teamid}/update")
    public ResponseEntity<ResponseForm> updateTeamInfo(@PathVariable UUID teamid, @RequestBody Team updateForm, HttpServletRequest request) {
        try {
            String refreshToken=request.getHeader("refresh-token");
            String authToken = request.getHeader("login-token");

            ServiceReturn re = teamService.update(teamid, updateForm, authToken,refreshToken);
            ResponseForm responseForm = ResponseForm.builder()
                    .message("update team")
                    .data(TeamData.builder().team(re.getData()).build())
                    .state(HttpStatus.OK.value())
                    .newToken(re.getNewToken())
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (RuntimeException | AuthenticationException |NotTeamLeaderException e) {
            ResponseForm errorResponseForm = ResponseForm.builder()
                    .message(e.getMessage())
                    .state(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping("/{teamId}/delete")
    public ResponseEntity<ResponseForm> deleteTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        try {

            String refreshToken=request.getHeader("refresh-token");
            String authToken = request.getHeader("login-token");
            ServiceReturn delete = teamService.delete(teamId, authToken, refreshToken);
            String newToken=delete.getNewToken();
            ResponseForm responseForm = ResponseForm.builder()
                    .message("팀을 삭제했습니다")
                    .newToken(newToken)
                    .state(HttpStatus.OK.value()).build();
            return ResponseEntity.ok().body(responseForm);
        } catch (AuthenticationException | RuntimeException | NotTeamLeaderException e) {
            ResponseForm errorResponseForm = ResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }


}
