package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.controller.PostTeamForm;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.repository.SpringDataJpaTeamLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import com.makedreamteam.capstoneback.repository.SpringDataTeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
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

    //순서
    //로그인 된 userId를 팀리더로 team을 만든다
    //위에서 만들어진 teamId를 통해 temaLang을 만든다
    //temaId와 로그인된 userId, teamLeader로 teamMember를 만든다
    public Team addPostTeam(PostTeamForm postTeamForm){
        try {
            Team team = newTeam(postTeamForm);
            Team save = springDataTeamRepository.save(team);
            TeamLang teamLang = newTeamLang(postTeamForm);
            teamLang.setTeamId(save.getTeamId());
            springDataJpaTeamLangRepository.save(teamLang);
            return team;
        } catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                 TransactionSystemException e) {
            System.out.println(e);
            /*NullPointerException: postTeamForm이 null인 경우 newTeam 및 newTeamLang 메서드에서 NullPointerException이 발생
              DataIntegrityViolationException: Team 엔티티 또는 TeamLang 엔티티의 제약 조건을 위반하여 데이터베이스에 저장할 수 없는 경우 DataIntegrityViolationException이 발생
              JpaSystemException: Team 엔티티 또는 TeamLang 엔티티의 속성값에 유효하지 않은 값이 포함되어 있거나, Team 엔티티와 TeamLang 엔티티의 관계 설정이 잘못된 경우 JpaSystemException이 발생
              TransactionSystemException: 트랜잭션 처리 중 예외가 발생하는 경우 TransactionSystemException이 발생*/
            // 예외 발생 시 처리할 코드 작성
            // 예를 들어, 로깅 등의 작업을 수행할 수 있습니다.
            // 예외 처리 후, 예외 발생을 호출자에게 알리기 위해 RuntimeException을 던질 수 있습니다.
            throw new RuntimeException("Failed to add team and team language.", e);
        }
    }
    public TeamLang newTeamLang(PostTeamForm postTeamForm){
        TeamLang teamLang = TeamLang.builder()
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
                .currentBackMember(postTeamForm.getCurrentBackMember())
                .currentFrontMember(postTeamForm.getCurrentFrontMember())
                .wantedBackEndMember(postTeamForm.getWantedBackEndMember())
                .wantedFrontMember(postTeamForm.getWantedFrontMember())
                .updateDate(postTeamForm.getUpdateDate())
                .createDate(postTeamForm.getCreateDate())
                .detail(postTeamForm.getDetail())
                .period(postTeamForm.getPeriod())
                .title(postTeamForm.getTitle())
                .build();
        return team;
    }



    public Team update(Long teamId,PostTeamForm postTeamForm){
        Optional<Team> optionalTeam = springDataTeamRepository.findById(teamId);
        Optional<TeamLang> optionalTeamLang = springDataJpaTeamLangRepository.findById(teamId);

        if (optionalTeam.isEmpty()) {
            throw new RuntimeException("Failed to update team: team not found");
        }

        if (optionalTeamLang.isEmpty()) {
            throw new RuntimeException("Failed to update team: teamLang not found");
        }

        Team team = optionalTeam.get();
        TeamLang teamLang = optionalTeamLang.get();

        Team updatedTeam = newTeam(postTeamForm);
        updatedTeam.setTeamId(team.getTeamId());
        springDataTeamRepository.save(updatedTeam);

        TeamLang updatedTeamLang = newTeamLang(postTeamForm);
        updatedTeamLang.setTeamId(teamLang.getTeamId());
        springDataJpaTeamLangRepository.save(updatedTeamLang);

        return updatedTeam;
    }
    public List<Team> findByTitleContaining(String title){
        if (title == null) {
            return new ArrayList<>();
        }
        try {
            return springDataTeamRepository.findByTitleContaining(title);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to retrieve teams by title containing '" + title + "'", e);
        }
    }
    public Optional<Team> findById(Long id){
        return springDataTeamRepository.findById(id);
        //optional은 이미 null값을 처리하는데 안전한 방법을 제공하기때문에 if문으 ㄹ사용하지 않아도된다
    }

    public List<Team> allPosts(Principal principal) {
        try {
            List<Team> allPost = springDataTeamRepository.findAll();
            for (Team team : allPost) {
                Optional<TeamLang> byId = springDataJpaTeamLangRepository.findById(team.getTeamId());
                if (byId.isEmpty())
                    throw new RuntimeException("Failed to retrieve TeamLang information for Team: " + team.getTeamId());
            }
            return allPost;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Failed to retrieve Team information from the database", e);
        }
    }








    public List<Long> calculateWeightOfLang(Long userid){
        List<TeamLang> allTeams=springDataJpaTeamLangRepository.findAll();
        UserLang user=springDataJpaUserLangRepository.findById(userid).get();
        List<Double> list=new ArrayList<>();
        List<Long> resultList=new ArrayList<>();
        HashMap<Double,Long> map=new HashMap<>();
        for(TeamLang teamlang : allTeams){
            double a=(teamlang.getAssembly()*user.getAssembly()+ teamlang.getC()*user.getC()+ teamlang.getCs()*user.getCs()+ teamlang.getVb()*user.getVb()+ teamlang.getCpp()*user.getCpp()+ teamlang.getJava()*user.getJava()+ teamlang.getJavascript()*user.getJavascript()+ teamlang.getPhp()*user.getPhp()+ teamlang.getPython()*user.getPython()+ teamlang.getSqllang()*user.getSqllang());
            map.put(a, teamlang.getTeamId());
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
            Optional<Team> team = springDataTeamRepository.findById(lang.getTeamId());
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


    public void delete(Long teamId) {
        Optional<Team> teamOptional = springDataTeamRepository.findById(teamId);
        Optional<TeamLang> teamLangOptional = springDataJpaTeamLangRepository.findById(teamId);
        if(teamOptional.isEmpty())
            throw new EntityNotFoundException("fail to find team with "+teamId);
        if(teamLangOptional.isEmpty())
            throw new EntityNotFoundException("fail to find teamLang with"+ teamId);
        Team team=teamOptional.get();
        TeamLang teamLang=teamLangOptional.get();
        springDataTeamRepository.delete(team);
        springDataJpaTeamLangRepository.delete(teamLang);
    }
}