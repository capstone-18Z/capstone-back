package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.PostTeam;
import com.makedreamteam.capstoneback.repository.SpringDataJpaPostTeamRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class PostTeamService {
    private final SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository;

    public PostTeamService(SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository) {
        this.springDataJpaPostTeamRepository = springDataJpaPostTeamRepository;
    }


    public List<PostTeam> findAll(){
        return springDataJpaPostTeamRepository.findAll();
    }
    public PostTeam addPostTeam(PostTeam postTeam){
        return springDataJpaPostTeamRepository.save(postTeam);
    }
    public List<PostTeam> findByTitleContaining(String title){
        return springDataJpaPostTeamRepository.findByTitleContaining(title);
    }
}
