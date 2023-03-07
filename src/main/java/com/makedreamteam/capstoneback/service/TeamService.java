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
import java.util.stream.Collectors;

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

    public ResponseFormForTeamInfo addPostTeam(PostTeamForm postTeamForm){
        try {
            Team team=newTeam(postTeamForm);
            TeamLang teamLang=newTeamLang(postTeamForm, team.getTeamid());
            springDataTeamRepository.save(team);
            springDataJpaTeamLangRepository.save(teamLang);
            return ResponseFormForTeamInfo.builder().message("팀을 추가했습니다").data(TeamData.builder().dataWithLogin(team).build()).build();
        }
        catch (Exception e){
            return ResponseFormForTeamInfo.builder().message("오류발생!! 팀을 추가할수없습니다 ").build();
        }
    }
    public TeamLang newTeamLang(PostTeamForm postTeamForm,Long teamId){
        TeamLang teamLang = TeamLang.builder()
                .teamid(teamId)
                .assembly(postTeamForm.getAssembly())
                .c(postTeamForm.getC())
                .cpp(postTeamForm.getCpp())
                .cs(postTeamForm.getCs())
                .php(postTeamForm.getPhp())
                .vb(postTeamForm.getVb())
                .java(postTeamForm.getJava())
                .python(postTeamForm.getPython())
                .javascript(postTeamForm.getJavascript())
                .sqllang(postTeamForm.getSqlLang())
                .build();

        return teamLang;
    }
    public Team newTeam(PostTeamForm postTeamForm){
        Team team = Team.builder()
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
        return team;
    }



    public ResponseFormForTeamInfo updatePostTeam(Team team,TeamLang teamLang,PostTeamForm postTeamForm){
        try {
            Team newTeam=newTeam(postTeamForm);
            newTeam.setTeamid(team.getTeamid());
            springDataTeamRepository.save(newTeam);
            TeamLang newTeamLang=newTeamLang(postTeamForm,teamLang.getTeamid());
            springDataJpaTeamLangRepository.save(newTeamLang);
            return ResponseFormForTeamInfo.builder().message("팀을 수정했습니다").data(TeamData.builder().dataWithLogin(team).build()).build();
        }
        catch (Exception e){
            return ResponseFormForTeamInfo.builder().message("오류발생!! 팀을 수정할수없습니다 ").build();
        }
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
            map.put(a, teamlang.getTeamid());
            list.add(a);
        }
        Collections.sort(list);
        for(int i=0;i<5 && list.size() > i ;i++){
            resultList.add(map.get(list.get(i)));
        }
        return resultList;
    }
    public List<Team> recommandTeams(Long userid,int count){
        List<TeamLang> teamLangs=springDataJpaTeamLangRepository.findAll();
        UserLang userLang=springDataJpaUserLangRepository.findById(userid).get();
        HashMap<Team,Integer> weight=new HashMap<>();
        for(TeamLang lang : teamLangs){
            Optional<Team> team = springDataTeamRepository.findById(lang.getTeamid());
            if(team.isPresent()) {
                weight.put(team.get(), lang.getC() * userLang.getC() + lang.getSqllang() * userLang.getSqllang() + lang.getCpp() * userLang.getCpp() + lang.getVb() * userLang.getVb() + lang.getCs() * userLang.getCs() + lang.getPhp() * userLang.getPhp() + lang.getPython() * userLang.getPython() + lang.getAssembly() * userLang.getAssembly() + lang.getJavascript() * userLang.getJavascript() + lang.getJava() * userLang.getJava());
            }
            else
                System.out.println("springDataTeamRepository.findById(lang.getTeamid()) is null");
        }
        List<Map.Entry<Team, Integer>> sortedList = new ArrayList<>(weight.entrySet());
        sortedList.sort(Comparator.comparing(Map.Entry::getValue));

        List<Team> result= sortedList.stream()
                .map(map->map.getKey()).limit(count).collect(Collectors.toList());


        return result;
    }

    public void  update(Long teamId,PostTeamForm form){
        Optional<Team> team = springDataTeamRepository.findById(teamId);
        Optional<TeamLang> teamLang = springDataJpaTeamLangRepository.findById(teamId);
        if(team.isPresent()){
            updatePostTeam(team.get(),teamLang.get(),form);
        }
        else{
            System.out.println("존재하는 팀이 없습니다.");
        }

    }
}
