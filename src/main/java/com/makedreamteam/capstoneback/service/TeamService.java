package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.controller.TeamData;
import com.makedreamteam.capstoneback.controller.PostTeamForm;
import com.makedreamteam.capstoneback.controller.ResponseFormForTeamInfo;
import com.makedreamteam.capstoneback.domain.Team;
import com.makedreamteam.capstoneback.domain.TeamLang;
import com.makedreamteam.capstoneback.domain.UserLang;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Transactional
public class TeamService{
    @Autowired
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;

    @Autowired
    private final SpringDataJpaUserLangRepository springDataJpaUserLangRepository;


    public TeamService(SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository, SpringDataJpaUserLangRepository springDataJpaUserLangRepository) {
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
        this.springDataJpaUserLangRepository = springDataJpaUserLangRepository;
    }




    public Team addPostTeam(PostTeamForm postTeamForm){

        Team team=Team.builder()
                .current_bm(postTeamForm.getCurrentBackMember())
                .current_fm(postTeamForm.getCurrentFrontMember())
                .wanted_bm(postTeamForm.getWantedBackEndMember())
                .wanted_fm(postTeamForm.getWantedFrontMember())
                .updatedate(postTeamForm.getUpdateDate())
                .createdate(postTeamForm.getCreateDate())
                .detail(postTeamForm.getDetail())
                .period(postTeamForm.getPeriod())
                .title(postTeamForm.getTitle())
                .build();
        springDataTeamRepository.save(team);


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


        return team;
    }
    public ResponseFormForTeamInfo findByTitleContaining(String title){
        List<Team> byTitleContaining = springDataTeamRepository.findByTitleContaining(title);
        if(byTitleContaining.size()<1)
            return ResponseFormForTeamInfo.builder()
                    .state(401)
                    .message(title+" 검색 결과 없음")
                    .build();

        return ResponseFormForTeamInfo.builder().message(title+" 검색 결과를 반환").state(201).data(TeamData.builder().dataWithoutLogin(byTitleContaining).build()).build();
    }
    public ResponseFormForTeamInfo findById(Long id){
        Team team = springDataTeamRepository.findById(id).get();

        if(team==null)
            return ResponseFormForTeamInfo.builder()
                    .message("일치하는 팀이 없습니다")
                    .state(401)
                    .build();
        return ResponseFormForTeamInfo.builder()
                .message("id와 일치하는 팀을 반환합니다")
                .state(201)
                .data(TeamData.builder().dataWithoutLogin(team).build())
                .build();
    }

    public ResponseFormForTeamInfo allPosts(Principal principal) {
        List<Team> allPost = springDataTeamRepository.findAll();

        if(allPost.size()<=0){
            ResponseFormForTeamInfo result= ResponseFormForTeamInfo.builder()
                    .state(401)
                    .message("현재 만들어진 팀이 업습니다.")
                    .build();
            return result;
        }
        if(principal==null)
            return  ResponseFormForTeamInfo.builder()
                    .state(201)
                    .data(TeamData.builder().dataWithoutLogin(allPost).build())
                    .message("모든 팀 리스트를 반환합니다.")
                    .build();
        return null;
    }

    public List<Long> calculateWeightOfLang(Long userid){
        List<TeamLang> allTeams=springDataJpaTeamLangRepository.findAll();
        UserLang user=springDataJpaUserLangRepository.findById(userid).get();
        List<Double> list=new ArrayList<>();
        List<Long> resultList=new ArrayList<>();
        HashMap<Double,Long> map=new HashMap<>();
        for(TeamLang teamlang : allTeams){
            double a=(teamlang.getAssembly()*user.getAssembly()+ teamlang.getC()*user.getC()+ teamlang.getCs()*user.getCs()+ teamlang.getVb()*user.getVb()+ teamlang.getCpp()*user.getCpp()+ teamlang.getJava()*user.getJava()+ teamlang.getJavascript()*user.getJavascript()+ teamlang.getPhp()*user.getPhp()+ teamlang.getPython()*user.getPython()+ teamlang.getSqllang()*user.getSqllang());
            double b=(teamlang.getAssembly()+ teamlang.getC()+ teamlang.getCs()+ teamlang.getVb()+ teamlang.getCpp()+ teamlang.getJava()+ teamlang.getJavascript()+ teamlang.getPhp()+ teamlang.getPython()+ teamlang.getSqllang());
            double result=a/b;
            System.out.println("result = " + result);
            map.put(result, teamlang.getTeamid());
            list.add(result);
        }
        Collections.sort(list);
        for(int i=0;i<5 && list.size() > i ;i++){
            resultList.add(map.get(list.get(i)));
        }
        for (Long a : resultList){
            System.out.println("a = " + a);
        }
        return resultList;
    }



}
