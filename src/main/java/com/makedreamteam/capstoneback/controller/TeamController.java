package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.*;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import com.makedreamteam.capstoneback.service.FileService;
import com.makedreamteam.capstoneback.service.ImageStorageService;
import com.makedreamteam.capstoneback.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping(value = "/teams", produces = "application/json;charset=UTF-8")
public class TeamController {

    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ImageStorageService imageStorage;
    private final FileService fileService;
    private final SpringDataTeamRepository springDataTeamRepository;

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

    @PostMapping(value = "/new",consumes = "multipart/form-data")
    public ResponseEntity<ResponseForm> createTeam(@RequestPart(value = "images", required = false) List<MultipartFile> images, @RequestPart("team") Team team, HttpServletRequest request) throws TokenException, DatabaseException, IOException {
        try {
            String refreshToken = request.getHeader("refresh-token");
            String accessToken = request.getHeader("login-token");

            List<String> imageUrls = null;
            if (images != null) {
                team.setImagePaths(teamService.uploadFile(images));
            }
            ResponseForm responseForm = teamService.addPostTeam(team, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.ok().body(error);
        }
    }

    @GetMapping("/search/{title}")//제목으로 포스트 검색
    public ResponseEntity<ResponseForm> searchPostByTitle(@PathVariable String title,HttpServletRequest request) {
       return null;
    }

    @GetMapping("/{id}")//포스트 페이지 접속
    public ResponseEntity<ResponseForm> findById(@PathVariable UUID id, HttpServletRequest request) {
            String authToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            ResponseForm team = teamService.findById(id, authToken, refreshToken);
            if(team.getData()==null){//team의  data가 null이라면 오류
                return ResponseEntity.badRequest().body(team);
            }
            if(team.isUpdatable()){//team이 업데이트 가능하다면 추천목록또한 같이 보낸다
                List<Member> members = teamService.recommendMembers(id, 2);
                TeamData teamData=(TeamData) team.getData();
                teamData.setRecommendList(members);
                team.setData(teamData);
                return ResponseEntity.badRequest().body(team);
            }
            else{
                return ResponseEntity.badRequest().body(team);
            }

    }

    //팀 정보 수정
    @PostMapping(value = "/{teamId}/update", consumes = "multipart/form-data")
    public ResponseEntity<ResponseForm> updateTeamInfo(@PathVariable UUID teamId,@RequestPart("team") Team updateForm, @RequestPart(value = "images", required = false) List<MultipartFile> images,HttpServletRequest request) {
        String accessToken= request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm update = teamService.update(teamId,updateForm, images,accessToken,refreshToken);
            return ResponseEntity.ok().body(update);
        }catch (NullPointerException | IOException e){
            ResponseForm build = ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(build);
        }
    }

    @PostMapping("/{teamId}/delete")//미구현
    public ResponseEntity<ResponseForm> deleteTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        String accessToken=request.getHeader("login-token");
        String refreshToken= request.getHeader("refresh-token");
        try {
            ResponseForm result = teamService.delete(teamId, accessToken, refreshToken);
            return ResponseEntity.ok().body(result);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    //임시 코드

    @PostMapping(value = "/test/add" , consumes = "multipart/form-data")
    public Team addTestTeam(@RequestPart("team") Team team,@RequestPart(value = "images", required = false) List<MultipartFile> images){
       return teamService.addNewTeam(team,images);
    }
    @PostMapping(value = "/test/{teamId}/update" , consumes = "multipart/form-data")
    public Team updateTest(@RequestPart("team") Team team,@RequestPart(value = "images", required = false) List<MultipartFile> images,@PathVariable UUID teamId){
        return teamService.updateTest(team,images,teamId);
    }
    @PostMapping("/test/{teamId}/delete")
    public void deleteTest(@PathVariable UUID teamId){
        teamService.deleteTest(teamId);
    }

    @PostMapping("/test/count-keyword")
    public List<Map<String,Integer>> countOfKeyword(){
        return teamService.countOfKeyword();
    }
}
