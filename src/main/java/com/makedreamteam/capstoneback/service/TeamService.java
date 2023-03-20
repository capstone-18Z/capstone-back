package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.controller.PostTeamForm;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
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
    @Autowired
    private final TeamMemberRepository teamMemberRepository;
    @Autowired
    private final MemberRepository memberRepository;
    public TeamService(SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository, SpringDataTeamRepository springDataTeamRepository, SpringDataJpaUserLangRepository springDataJpaUserLangRepository, TeamMemberRepository teamMemberRepository, MemberRepository memberRepository) {
        this.springDataJpaTeamLangRepository = springDataJpaTeamLangRepository;
        this.springDataTeamRepository = springDataTeamRepository;
        this.springDataJpaUserLangRepository = springDataJpaUserLangRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.memberRepository = memberRepository;
    }

    //순서
    //로그인 된 userId를 팀리더로 team을 만든다
    //위에서 만들어진 teamId를 통해 temaLang을 만든다
    //temaId와 로그인된 userId, teamLeader로 teamMember를 만든다
    public Team addPostTeam(PostTeamForm postTeamForm,String authToken){
        if(authToken==null)
            throw new RuntimeException("로그인 상태가 아닙니다.");
        try {

            UUID teamLeader=getUserIdFromToken(authToken);

            Team team = newTeam(postTeamForm);
            team.setTeamLeader(teamLeader);
            Team savedTeam = springDataTeamRepository.save(team);
            UUID teamId=savedTeam.getTeamId();

            TeamLang teamLang = newTeamLang(postTeamForm);
            teamLang.setTeamId(teamId);
            springDataJpaTeamLangRepository.save(teamLang);

            TeamMember teamMember=TeamMember.builder().teamId(teamId).teamLeader(teamLeader).userId(teamLeader).build();
            teamMemberRepository.save(teamMember);

            return team;
        } catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                 TransactionSystemException e) {
            /*NullPointerException: postTeamForm이 null인 경우 newTeam 및 newTeamLang 메서드에서 NullPointerException이 발생
              DataIntegrityViolationException: Team 엔티티 또는 TeamLang 엔티티의 제약 조건을 위반하여 데이터베이스에 저장할 수 없는 경우 DataIntegrityViolationException이 발생
              JpaSystemException: Team 엔티티 또는 TeamLang 엔티티의 속성값에 유효하지 않은 값이 포함되어 있거나, Team 엔티티와 TeamLang 엔티티의 관계 설정이 잘못된 경우 JpaSystemException이 발생
              TransactionSystemException: 트랜잭션 처리 중 예외가 발생하는 경우 TransactionSystemException이 발생*/
            // 예외 발생 시 처리할 코드 작성
            // 예를 들어, 로깅 등의 작업을 수행할 수 있습니다.
            // 예외 처리 후, 예외 발생을 호출자에게 알리기 위해 RuntimeException을 던질 수 있습니다.
            throw new RuntimeException("Failed to add team and team language.", e);
        }
        catch (JwtException ex) {
            throw new RuntimeException(ex.getMessage());
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
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
                .teamLeader(postTeamForm.getTeamLeader())
                .writer(postTeamForm.getWriter())
                .build();
        return team;
    }



    public Team update(UUID teamId,PostTeamForm postTeamForm,String authToken) throws AuthenticationException {
        Optional<Team> optionalTeam = springDataTeamRepository.findById(teamId);
        Optional<TeamLang> optionalTeamLang = springDataJpaTeamLangRepository.findById(teamId);
        UUID userId=getUserIdFromToken(authToken);
        if (optionalTeam.isEmpty()) {
            throw new RuntimeException("Failed to update team: team not found");
        }

        if (optionalTeamLang.isEmpty()) {
            throw new RuntimeException("Failed to update team: teamLang not found");
        }
        if(!userId.equals(optionalTeam.get().getTeamLeader())){
            throw new RuntimeException("팀 리더만 수정할수있습니다.");
        }
        try {
            Team team = optionalTeam.get();
            TeamLang teamLang = optionalTeamLang.get();

            Team updatedTeam = newTeam(postTeamForm);
            updatedTeam.setTeamId(team.getTeamId());
            updatedTeam.setTeamLeader(userId);
            springDataTeamRepository.save(updatedTeam);

            TeamLang updatedTeamLang = newTeamLang(postTeamForm);
            updatedTeamLang.setTeamId(teamLang.getTeamId());
            springDataJpaTeamLangRepository.save(updatedTeamLang);

            return updatedTeam;
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }

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
    public Optional<Team> findById(UUID id){
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
    public List<Member> recommandUsers(UUID teamId,int count){
        List<UserLang> userLangs=springDataJpaUserLangRepository.findAll();
        TeamLang teamLang=springDataJpaTeamLangRepository.findById(teamId).get();
        HashMap<Member,Integer> weight=new HashMap<>();
        for(UserLang lang : userLangs){
            Optional<Member> member = memberRepository.findById(lang.getUserid());
            if(member.isPresent()) {
                weight.put(member.get(), lang.getC() * teamLang.getC() + lang.getSqllang() * teamLang.getSqllang() + lang.getCpp() * teamLang.getCpp() + lang.getVb() * teamLang.getVb() + lang.getCs() * teamLang.getCs() + lang.getPhp() * teamLang.getPhp() + lang.getPython() * teamLang.getPython() + lang.getAssembly() * teamLang.getAssembly() + lang.getJavascript() * teamLang.getJavascript() + lang.getJava() * teamLang.getJava());
            }
            else
                System.out.println("springDataTeamRepository.findById(lang.getTeamid()) is null");
        }
        List<Map.Entry<Member, Integer>> sortedList = new ArrayList<>(weight.entrySet());
        sortedList.sort(Comparator.comparing(Map.Entry::getValue));

        List<Member> result= sortedList.stream()
                .map(map->map.getKey()).limit(count).collect(Collectors.toList());


        return result;
    }

    public void delete(UUID teamId,String authToken) throws AuthenticationException {
        Optional<Team> teamOptional = springDataTeamRepository.findById(teamId);
        Optional<TeamLang> teamLangOptional = springDataJpaTeamLangRepository.findById(teamId);
        UUID userId=getUserIdFromToken(authToken);
        if(teamOptional.isEmpty())
            throw new EntityNotFoundException("fail to find team with "+teamId);
        if(teamLangOptional.isEmpty())
            throw new EntityNotFoundException("fail to find teamLang with"+ teamId);
        if(!userId.equals(teamOptional.get().getTeamLeader())) {
            throw new RuntimeException("팀 리더만 팀을 삭제할수있습니다.");
        }
        Team team=teamOptional.get();
        TeamLang teamLang=teamLangOptional.get();
        List<TeamMember> teamMemberList=teamMemberRepository.findAllByTeamId(team.getTeamId());
        teamMemberRepository.deleteAll(teamMemberList);
        springDataTeamRepository.delete(team);
        springDataJpaTeamLangRepository.delete(teamLang);
    }
    public UUID getUserIdFromToken(String token) throws AuthenticationException {
        if (token == null) {
            throw new AuthenticationException("Invalid Authorization header");
        }

        Jws<Claims> claimsJws;
        claimsJws = Jwts.parser().setSigningKey("test").parseClaimsJws(token);

        Claims claims = claimsJws.getBody();
        String username = claims.getSubject();
        Date expirationDate = claims.getExpiration();

        if (username == null || expirationDate == null || expirationDate.before(new Date())) {
            throw new AuthenticationException("Invalid JWT claims");
        }

        return UUID.fromString((String)claims.get("sub"));
    }

}
