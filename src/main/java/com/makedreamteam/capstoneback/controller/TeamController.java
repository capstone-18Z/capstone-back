package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.form.*;
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

    @GetMapping("")
    public ResponseEntity<ResponseForm> allPost(HttpServletRequest request, @RequestParam("page") int page) {
        //check login
        try {
            String authToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            List<Team> recommendTeams = null;
            List<Team> teams = teamService.allPosts(authToken, refreshToken, page);
            int totalPage = teamService.getTotalPage(12);
            ResponseForm responseForm = ResponseForm.builder().metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).message("모든 팀을 조회합니다").state(HttpStatus.OK.value()).data(teams).build();
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
            ResponseForm responseFormOK = teamService.updateTeam(team, images, teamId, accessToken, refreshToken);
            return ResponseEntity.ok(responseFormOK);
        } catch (RuntimeException e) {
            ResponseForm responseFormError = ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(responseFormError);
        }

    }

    @PostMapping("/{teamId}/delete")
    public ResponseEntity<ResponseForm> deleteTest(@PathVariable UUID teamId, @RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        try {
            ResponseForm responseFormOK = teamService.deleteTeam(teamId, accessToken, refreshToken);
            return ResponseEntity.ok(responseFormOK);
        } catch (RuntimeException e) {
            ResponseForm responseFormError = ResponseForm.builder().message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(responseFormError);
        }

    }

    @GetMapping("/{id}")//포스트 페이지 접속
    public ResponseEntity<ResponseForm> findById(@PathVariable UUID id, HttpServletRequest request) {
        String accessToken= request.getHeader("login-token");
        String refreshToken=request.getHeader("refresh-token");
        ResponseForm team = teamService.findById(id, accessToken, refreshToken);
        if (team.getData() == null) {//team의  data가 null이라면 오류
            return ResponseEntity.badRequest().body(team);
        }
        return ResponseEntity.ok().body(team);

    }

    @PostMapping("/{teamId}/recommend")
    public ResponseEntity<ResponseForm> findRecommendMember(@PathVariable UUID teamId, @RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        ResponseForm recommendList = teamService.recommendMembers(teamId, accessToken, refreshToken);
        return ResponseEntity.ok().body(recommendList);
    }


    @GetMapping("/search/{title}")//제목으로 포스트 검색
    public ResponseEntity<ResponseForm> searchPostByTitle(@PathVariable String title, @RequestParam("page") int page) {
        ResponseForm responseForm = teamService.postListByTitle(title, page);
        return ResponseEntity.ok(responseForm);
    }

    @GetMapping("/myteams")
    public ResponseEntity<ResponseForm> getAllMyTeams(@RequestHeader("login-token") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        try {
            ResponseForm responseForm = teamService.getAllMyTeams(accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<ResponseForm> doFilteringTeams(@RequestParam("search") String search,@RequestParam("category") List<String> category,@RequestParam("subject") List<String> subject, @RequestParam("rule") List<String> rule,@RequestParam("page") int page){

        ResponseForm responseForm=teamService.doFilteringTeams(category,subject,rule,search,page);

        System.out.println("search : "+ search);
        System.out.println("category : "+ category);
        System.out.println("subject : "+subject);
        System.out.println("rule : "+ rule);

        System.out.println("page : "+page);
        return ResponseEntity.ok().body(responseForm);
    }





}
