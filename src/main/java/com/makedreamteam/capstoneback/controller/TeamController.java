package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.*;
import com.makedreamteam.capstoneback.service.FileService;
import com.makedreamteam.capstoneback.service.ImageStorageService;
import com.makedreamteam.capstoneback.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/teams", produces = "application/json;charset=UTF-8")
public class TeamController {

    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ImageStorageService imageStorage;
    private final FileService fileService;

    @Autowired
    public TeamController(TeamService postTeamService, JwtTokenProvider jwtTokenProvider, ImageStorageService imageStorage, FileService fileService) {
        this.teamService = postTeamService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.imageStorage = imageStorage;
        this.fileService = fileService;
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

    //팀 정보 수정
    @PostMapping(value = "/{teamId}/update", consumes = "multipart/form-data")
    public ResponseEntity<ResponseForm> updateTeamInfo(@PathVariable UUID teamId,@RequestPart("team") Team updateForm, @RequestPart("images") List<MultipartFile> images,HttpServletRequest request) {
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
    @PostMapping(value = "/new",consumes = "multipart/form-data")
    public ResponseEntity<ResponseForm> createTeam(@RequestPart("images") List<MultipartFile> images,@RequestPart("team") Team team,HttpServletRequest request) throws TokenException, DatabaseException, IOException {
        try {
            String refreshToken = request.getHeader("refresh-token");
            String accessToken = request.getHeader("login-token");
            List<String> imageUrls = null;
            if (images != null)
                imageUrls = teamService.uploadFile(images);
            if(imageUrls!=null)
                team.setImagePaths(imageUrls);
            ResponseForm responseForm = teamService.addPostTeam(team, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.ok().body(error);
        }
    }


}
