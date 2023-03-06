package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.service.TeamService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@CrossOrigin
public class TeamController {

    private final TeamService teamService;
    @Autowired
    public TeamController(TeamService postTeamService) {
        this.teamService = postTeamService;
    }
    @GetMapping("/teams")
    public ResponseFormForTeamInfo allPost(Principal principal){
        String userName=principal!=null ? principal.getName() : "";
        return teamService.allPosts(principal);
    }

    @GetMapping("/teams/search/{title}")
    public ResponseFormForTeamInfo searchPostByTitle(@PathVariable String title){
        return teamService.findByTitleContaining(title);
    }

    @PostMapping("/teams/{id}")
    public ResponseFormForTeamInfo findById(@PathVariable Long id){
        return teamService.findById(id);
    }

    @PostMapping("/team/new")
    public ResponseFormForTeamInfo savePostTeam(@RequestBody PostTeamForm postTeamForm){
        System.out.println(postTeamForm.toString());
        return teamService.addPostTeam(postTeamForm);
    }
    //팀 정보 수정



}
