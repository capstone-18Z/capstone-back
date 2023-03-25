package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.NotTeamLeaderException;
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
    private final SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    private final SpringDataJpaUserLangRepository springDataJpaUserLangRepository;
    @Autowired
    private final TeamMemberRepository teamMemberRepository;
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;


    public TeamService(  SpringDataTeamRepository springDataTeamRepository, SpringDataJpaUserLangRepository springDataJpaUserLangRepository, TeamMemberRepository teamMemberRepository, MemberRepository memberRepository, PostMemberRepository postMemberRepository) {
        this.springDataTeamRepository = springDataTeamRepository;
        this.springDataJpaUserLangRepository = springDataJpaUserLangRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.memberRepository = memberRepository;
        this.postMemberRepository = postMemberRepository;
    }

    //순서
    //로그인 된 userId를 팀리더로 team을 만든다
    //위에서 만들어진 teamId를 통해 temaLang을 만든다
    //temaId와 로그인된 userId, teamLeader로 teamMember를 만든다
    public Team addPostTeam(Team form,String authToken){

        if(authToken==null)
            throw new RuntimeException("로그인 상태가 아닙니다.");

        try {
            UUID teamLeader= checkUserIdAndToken(authToken);
            List<Team> teams=springDataTeamRepository.findByTeamLeader(teamLeader);
            if(teams.size()==3){
                throw new RuntimeException("4개 이상의 팀을 만들 수 없습니다.");
            }
            Team team = newTeam(form);
            team.setTeamLeader(teamLeader);
            Team savedTeam = springDataTeamRepository.save(team);
            UUID teamId=savedTeam.getTeamId();

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

    public Team newTeam(Team form){
        Team team = Team.builder()
                .currentBackMember(form.getCurrentBackMember())
                .currentFrontMember(form.getCurrentFrontMember())
                .wantedBackEndMember(form.getWantedBackEndMember())
                .wantedFrontMember(form.getWantedFrontMember())
                .currentBasicMember(form.getCurrentBasicMember())
                .wantedBasicMember(form.getWantedBasicMember())
                .updateDate(form.getUpdateDate())
                .createDate(form.getCreateDate())
                .detail(form.getDetail())
                .period(form.getPeriod())
                .title(form.getTitle())
                .writer(form.getWriter())
                .field(form.getField())
                .cs(form.getCs())
                .cpp(form.getCpp())
                .c(form.getC())
                .python(form.getPython())
                .php(form.getPhp())
                .javascript(form.getJavascript())
                .vb(form.getVb())
                .java(form.getJava())
                .assembly(form.getAssembly())
                .sqllang(form.getSqllang())
                .build();
        return team;
    }



    public Team update(UUID teamId,Team form,String authToken) throws AuthenticationException, NotTeamLeaderException {
        Optional<Team> optionalTeam = springDataTeamRepository.findById(teamId);
        if (optionalTeam.isEmpty()) {
            throw new RuntimeException("팀이 존재하지 않습니다");
        }
        UUID userId= checkUserIdAndToken(authToken,optionalTeam);
        try {
            Team team = optionalTeam.get();


            Team updatedTeam = newTeam(form);
            updatedTeam.setTeamId(team.getTeamId());
            updatedTeam.setTeamLeader(userId);
            springDataTeamRepository.save(updatedTeam);


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
            return springDataTeamRepository.findAll();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Failed to retrieve Team information from the database", e);
        }
    }


    public List<Member> recommendUsers(UUID teamId, int count, String token) throws NotTeamLeaderException {
        try {
            Optional<Team> optionalTeam=springDataTeamRepository.findById(teamId);
            if(optionalTeam.isEmpty()){
                throw new RuntimeException("팀이 존재하지 않습니다.("+teamId+")");
            }
            checkUserIdAndToken(token,optionalTeam);


            List<UserLang> users=new ArrayList<>();
            List<PostMember> postMembers=postMemberRepository.findAll();
            for (PostMember postMember:postMembers){
                users.add(springDataJpaUserLangRepository.findById(postMember.getUserId()).get());
            }
            Team team=optionalTeam.get();
            HashMap<Member,Integer> weight=new HashMap<>();
            for(UserLang lang : users){
                Optional<Member> member = memberRepository.findById(lang.getUserid());
                if(member.isPresent()) {
                    weight.put(member.get(), lang.getC() * team.getC() + lang.getSqllang() * team.getSqllang() + lang.getCpp() * team.getCpp() + lang.getVb() * team.getVb() + lang.getCs() * team.getCs() + lang.getPhp() * team.getPhp() + lang.getPython() * team.getPython() + lang.getAssembly() * team.getAssembly() + lang.getJavascript() * team.getJavascript() + lang.getJava() * team.getJava());
                }
                else
                    System.out.println("springDataTeamRepository.findById(lang.getTeamid()) is null");
            }
            List<Map.Entry<Member, Integer>> sortedList = new ArrayList<>(weight.entrySet());


            Collections.sort(sortedList, new Comparator<Map.Entry<Member, Integer>>() {
                @Override
                public int compare(Map.Entry<Member, Integer> o1, Map.Entry<Member, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            List<Member> result= sortedList.stream()
                    .map(map->map.getKey()).limit(count).collect(Collectors.toList());

            System.out.println("----정렬 결과---");
            for (Member member: result){
                System.out.println("member.getEmail() = " + member.getEmail());
            }
            System.out.println("-------------------");
            return result;
        }catch (NotTeamLeaderException e){
            throw new NotTeamLeaderException(e.getMessage());
        }catch (AuthenticationException e){
            throw new RuntimeException(e);
        }

    }

    public void delete(UUID teamId,String authToken) throws AuthenticationException, NotTeamLeaderException {
        Optional<Team> teamOptional = springDataTeamRepository.findById(teamId);

        if(teamOptional.isEmpty())
            throw new EntityNotFoundException("fail to find team with "+teamId);

        checkUserIdAndToken(authToken,teamOptional);
        Team team=teamOptional.get();
        List<TeamMember> teamMemberList=teamMemberRepository.findAllByTeamId(team.getTeamId());
        teamMemberRepository.deleteAll(teamMemberList);
        springDataTeamRepository.delete(team);
    }
    public UUID checkUserIdAndToken(String token, Optional<Team> team) throws AuthenticationException, NotTeamLeaderException {
        if (token == null) {
            throw new AuthenticationException(" 토큰이 없습니다.");
        }

        Jws<Claims> claimsJws;
        claimsJws = Jwts.parser().setSigningKey("test").parseClaimsJws(token);

        Claims claims = claimsJws.getBody();
        String username = claims.getSubject();
        Date expirationDate = claims.getExpiration();

        if (username == null || expirationDate == null || expirationDate.before(new Date())) {
            throw new AuthenticationException("Invalid JWT claims");
        }
        if(team.isPresent())
            if(!UUID.fromString((String)claims.get("sub")).equals(team.get().getTeamLeader()) && !claims.get("roles").equals(Role.ROLE_ADMIN)) {
                throw new NotTeamLeaderException("권한이 없습니다.");
            }


        return UUID.fromString((String)claims.get("sub"));
    }
    public UUID checkUserIdAndToken(String token) throws AuthenticationException {
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
