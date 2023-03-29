package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.RefreshToken;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.*;
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
            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            ResponseForm team = teamService.findById(id, authToken, refreshToken);
            if(team.getData()==null){//team의  data가 null이라면 오류
                return ResponseEntity.badRequest().body(team);
            }
            if(team.isUpdatable()){//team이 업데이트 가능하다면 추천목록또한 같이 보낸다
                List<PostMember> members = teamService.recommendUsers(id, 5);
                TeamData teamData=(TeamData) team.getData();
                teamData.setRecommendList(members);
                team.setData(teamData);
                return ResponseEntity.badRequest().body(team);
            }
            else{
                return ResponseEntity.badRequest().body(team);
            }

    }


    @PostMapping("/new")
    public ResponseEntity<ResponseForm> addNewTeam(@RequestBody Team form , HttpServletRequest request) {
        try {

            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            ResponseForm responseForm =  teamService.addPostTeam(form, authToken,refreshToken);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
        } catch (RuntimeException | TokenException | DatabaseException e) {
            ResponseForm errorResponse = ResponseForm.builder()
                    .message("Failed to create team: " + e.getMessage())
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    //팀 정보 수정
    @PostMapping("/{teamid}/update")
    public ResponseEntity<ResponseForm> updateTeamInfo(@PathVariable UUID teamid, @RequestBody Team updateForm, HttpServletRequest request,List<String> keywordList) {
        try {
            String refreshToken=request.getHeader("refresh-token");
            String authToken = request.getHeader("login-token");

            ServiceReturn re = teamService.update(teamid, updateForm, authToken,refreshToken);
            ResponseForm responseForm = ResponseForm.builder()
                    .message("update team")
                    .data(TeamData.builder().team(re.getData()).build())
                    .state(HttpStatus.OK.value())
                    .updatable(true)
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (RuntimeException | AuthenticationException | NotTeamLeaderException e) {
            ResponseForm errorResponseForm = ResponseForm.builder()
                    .message(e.getMessage())
                    .state(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }catch (LoginTokenExpiredException e){
            ResponseForm newTokenResponse = ResponseForm.builder()
                    .message("새로운 토큰 발급이 필요합니다")
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(newTokenResponse);
        } catch (RefreshTokenExpiredException e) {
            ResponseForm newTokenResponse = ResponseForm.builder()
                    .message("새로운 로그인이 필요합니다")
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(newTokenResponse);
        }
    }

    @PostMapping("/{teamId}/delete")
    public ResponseEntity<ResponseForm> deleteTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        try {

            String refreshToken=request.getHeader("refresh-token");
            String authToken = request.getHeader("login-token");
            ServiceReturn delete = teamService.delete(teamId, authToken, refreshToken);

            ResponseForm responseForm = ResponseForm.builder()
                    .message("팀을 삭제했습니다")
                    .state(HttpStatus.OK.value()).build();
            return ResponseEntity.ok().body(responseForm);
        } catch (AuthenticationException | RuntimeException | NotTeamLeaderException e) {
            ResponseForm errorResponseForm = ResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        } catch (LoginTokenExpiredException e){
            ResponseForm newTokenResponse = ResponseForm.builder()
                    .message("새로운 토큰 발급이 필요합니다")
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(newTokenResponse);
        } catch (RefreshTokenExpiredException e) {
            ResponseForm newTokenResponse = ResponseForm.builder()
                    .message("새로운 로그인이 필요합니다")
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(newTokenResponse);
        }
    }



}
