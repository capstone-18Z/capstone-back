package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class TeamController {

    private final TeamService teamService;
    @Autowired
    public TeamController(TeamService postTeamService) {
        this.teamService = postTeamService;
    }
    @GetMapping("/teams")
    public ResponseFormForTeamInfo allPost(){


        return teamService.allPost();
    }

    @PostMapping("/teams/search/{title}")
    public ResponseFormForTeamInfo searchPostByTitle(@PathVariable String title){
        return teamService.findByTitleContaining(title);
    }

    @PostMapping("/teams/{id}")
    public ResponseFormForTeamInfo findById(@PathVariable Long id){
        return teamService.findById(id);
    }

    @PostMapping("/team/new")
    public Team savePostTeam(@RequestBody PostTeamForm postTeamForm){
        System.out.println(postTeamForm.toString());
        return teamService.addPostTeam(postTeamForm);
    }

}
