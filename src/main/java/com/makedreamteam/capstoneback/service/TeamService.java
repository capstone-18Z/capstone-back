package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.controller.PostTeamForm;
import com.makedreamteam.capstoneback.domain.PostTeam;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamLang;
import com.makedreamteam.capstoneback.repository.SpringDataJpaPostTeamRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class TeamService {
    @Autowired
    private final SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository;
    @Autowired
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;


    public TeamService(SpringDataJpaPostTeamRepository springDataJpaPostTeamRepository, SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository) {
        this.springDataJpaPostTeamRepository = springDataJpaPostTeamRepository;
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
    }


    public List<PostTeam> findAll(){
        return springDataJpaPostTeamRepository.findAll();
    }

    public PostTeam addPostTeam(PostTeamForm postTeamForm){

        Team team=Team.builder()
                .current_bm(postTeamForm.getCurrentBackMember())
                .current_fm(postTeamForm.getCurrentFrontMember())
                .wanted_bm(postTeamForm.getWantedBackEndMember())
                .wanted_fm(postTeamForm.getWantedFrontMember())
                .build();
        springDataTeamRepository.save(team);

        PostTeam postTeam=new PostTeam();
        postTeam.setTeamid(team.getTeamid());
        postTeam.setUpdatedate(postTeamForm.getUpdateDate());
        postTeam.setCreatedate(postTeamForm.getCreateDate());
        postTeam.setTitle(postTeamForm.getTitle());
        postTeam.setDetail(postTeamForm.getDetail());
        postTeam.setPeriod(postTeamForm.getPeriod());
        postTeam=springDataJpaPostTeamRepository.save(postTeam);

        TeamLang teamLang=new TeamLang();
        teamLang.setTeamid(team.getTeamid());
        teamLang.setAssembly(postTeamForm.getAssembly());
        teamLang.setC(postTeamForm.getC());
        teamLang.setCpp(postTeamForm.getCpp());
        teamLang.setJava(postTeamForm.getJava());
        teamLang.setPhp(postTeamForm.getPhp());
        teamLang.setJavascript(postTeamForm.getJavascript());
        teamLang.setSqllang(postTeamForm.getSqlLang());
        teamLang.setCs(postTeamForm.getCs());
        springDataJpaTeamLangRepository.save(teamLang);


        return postTeam;
    }
    public List<PostTeam> findByTitleContaining(String title){
        return springDataJpaPostTeamRepository.findByTitleContaining(title);
    }
}
