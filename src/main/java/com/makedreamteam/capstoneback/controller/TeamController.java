package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.form.*;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.service.FileService;
import com.makedreamteam.capstoneback.service.ImageStorageService;
import com.makedreamteam.capstoneback.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping(value = "/teams", produces = "application/json;charset=UTF-8")
public class TeamController {
    private final TeamService teamService;

    @GetMapping("/page/{page}")
    public ResponseEntity<ResponseForm> allPost(HttpServletRequest request, @PathVariable int page) {
        //check login
        try {
            String authToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            List<Team> recommendTeams = null;
            List<Team> teams = teamService.allPosts(authToken, refreshToken, page);
            ResponseForm responseForm = ResponseForm.builder().message("모든 팀을 조회합니다").state(HttpStatus.OK.value()).data(teams).build();
            return ResponseEntity.ok().body(responseForm);
        } catch (RuntimeException e) {
            ResponseForm errorResponseForm = ResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping(value = "/new", consumes = "multipart/form-data")
    public ResponseEntity<ResponseForm> addTestTeam(@RequestPart("team") Team team, @RequestPart(value = "images", required = false) List<MultipartFile> images, @RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        try {
            ResponseForm responseFormOK = teamService.addNewTeam(team, images, accessToken, refreshToken);
            return ResponseEntity.ok(responseFormOK);
        } catch (RuntimeException e) {
            ResponseForm responseFormError = ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(responseFormError);
        }
    }

    @PostMapping(value = "/{teamId}/update", consumes = "multipart/form-data")
    public ResponseEntity<ResponseForm> updateTest(@RequestPart("team") Team team, @RequestPart(value = "images", required = false) List<MultipartFile> images, @PathVariable UUID teamId, @RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        try {
            ResponseForm responseFormOK = teamService.updateTest(team, images, teamId, accessToken, refreshToken);
            return ResponseEntity.ok(responseFormOK);
        } catch (RuntimeException e) {
            ResponseForm responseFormError = ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(responseFormError);
        }

    }

    @PostMapping("/{teamId}/delete")
    public ResponseEntity<ResponseForm> deleteTest(@PathVariable UUID teamId, @RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        try {
            ResponseForm responseFormOK = teamService.deleteTest(teamId, accessToken, refreshToken);
            return ResponseEntity.ok(responseFormOK);
        } catch (RuntimeException e) {
            ResponseForm responseFormError = ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(responseFormError);
        }

    }

    @GetMapping("/{id}")//포스트 페이지 접속
    public ResponseEntity<ResponseForm> findById(@PathVariable UUID id, @RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        ResponseForm team = teamService.findById(id, accessToken, refreshToken);
        if (team.getData() == null) {//team의  data가 null이라면 오류
            return ResponseEntity.badRequest().body(team);
        }
        return ResponseEntity.ok().body(team);


    }
    @PostMapping("/{teamId}/recommend")
    public ResponseEntity<ResponseForm> findRecommendMember(@PathVariable UUID teamId,@RequestHeader("login-token") String accessToken,@RequestHeader("refresh-token")String refreshToken){
        ResponseForm recommendList=teamService.recommendMembers2(teamId,accessToken,refreshToken);
        return ResponseEntity.ok().body(recommendList);
    }


    @GetMapping("/search/{title}")//제목으로 포스트 검색
    public ResponseEntity<ResponseForm> searchPostByTitle(@PathVariable String title, HttpServletRequest request) {
        return null;
    }



}
