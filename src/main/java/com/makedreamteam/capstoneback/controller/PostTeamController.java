package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.service.PostTeamService;
import com.makedreamteam.capstoneback.domain.PostTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PostTeamController {

    private final PostTeamService postTeamService;
    @Autowired
    public PostTeamController(PostTeamService postTeamService) {
        this.postTeamService = postTeamService;
    }
    @GetMapping("/postteams")
    @ResponseBody
    public List<PostTeam> allPost(){
        System.out.println(234);
        return postTeamService.findAll();
    }

    @PostMapping("/postteam/{title}")
    @ResponseBody
    public List<PostTeam> searchPostByTitle(@PathVariable String title){
        System.out.println("아아앙 작동중작동중작동중작동중작동중");
        return postTeamService.findByTitleContaining(title);
    }

    @PostMapping("/postteam/new")
    @ResponseBody
    public PostTeam savePostTeam(PostTeam postTeam){
        return postTeamService.addPostTeam(postTeam);
    }

}
