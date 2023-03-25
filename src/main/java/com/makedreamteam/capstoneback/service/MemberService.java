package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;
    @Autowired
    private final SpringDataJpaUserLangRepository springDataJpaUserLangRepository;
    @Autowired
    private final SpringDataJpaTeamLangRepository springDataJpaTeamLangRepository;

    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;


    public PostMember PostJoin(PostMember post){
        try{
            PostMember save = postMemberRepository.save(post);
            return save;
        }catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                TransactionSystemException e) {
            throw new RuntimeException("Failed to add", e);
        }
    }
    public Member MemberJoin(Member post){
        try{
            if(checkEmailDuplicate(post.getEmail())==false && checkNicknameDuplicate(post.getNickname())==false) {
                Member save = memberRepository.save(post);
                return save;
            }
            else if (checkNicknameDuplicate(post.getNickname())){
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
            else{
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        }catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                TransactionSystemException e) {
            throw new RuntimeException("Failed to add", e);
        }
    }

    public List<Team> recommandTeams(UUID userId,int count){
        List<TeamLang> teamLangs=springDataJpaTeamLangRepository.findAll();
        UserLang userLang=springDataJpaUserLangRepository.findByUserid(userId).get();
        HashMap<Team,Integer> weight = new HashMap<>();
        for(TeamLang lang : teamLangs){
            Optional<Team> team = springDataTeamRepository.findById(lang.getTeamId());
            if(team.isPresent()) {
                weight.put(team.get(), lang.getC() * userLang.getC() + lang.getSqllang() * userLang.getSqllang() + lang.getCpp() * userLang.getCpp() + lang.getVb() * userLang.getVb() + lang.getCs() * userLang.getCs() + lang.getPhp() * userLang.getPhp() + lang.getPython() * userLang.getPython() + lang.getAssembly() * userLang.getAssembly() + lang.getJavascript() * userLang.getJavascript() + lang.getJava() * userLang.getJava());
            }
            else
                System.out.println("springDataTeamRepository.findById(lang.getTeamid()) is null");
        }
        List<Map.Entry<Team, Integer>> sortedList = new ArrayList<>(weight.entrySet());


        Collections.sort(sortedList, new Comparator<Map.Entry<Team, Integer>>() {
            @Override
            public int compare(Map.Entry<Team, Integer> o1, Map.Entry<Team, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<Team> result= sortedList.stream()
                .map(map->map.getKey()).limit(count).collect(Collectors.toList());

        System.out.println("----정렬 결과---");
        for (Team team: result){
            System.out.println("member.getEmail() = " + team.getTeamId());
        }
        System.out.println("-------------------");
        return result;
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


    public boolean checkEmailDuplicate(String email){
        return memberRepository.existsByEmail(email);
    }
    public boolean checkNicknameDuplicate(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
}
