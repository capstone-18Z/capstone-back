package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.service.TeamService;
import com.makedreamteam.capstoneback.domain.PostTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    public List<PostTeam> allPost(){
        return teamService.findAll();
    }

    @GetMapping("/teams/search/{title}")
    public List<PostTeam> searchPostByTitle(@PathVariable String title){
        return teamService.findByTitleContaining(title);
    }

    @PostMapping("/team/new")
    public PostTeam savePostTeam(@RequestBody PostTeamForm postTeamForm){
        System.out.println(postTeamForm.toString());
        return teamService.addPostTeam(postTeamForm);
    }

}
