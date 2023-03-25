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
    private final SpringDataTeamRepository springDataTeamRepository;


    public PostMember PostJoin(PostMember post, String authToken){
        if(authToken==null)
            throw new RuntimeException("로그인 상태가 아닙니다.");
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

    public Member newMember(Member form){
        Member member = Member.builder()
                .email(form.getEmail())
                .nickname(form.getNickname())
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
        return member;
    }

    public Member update(UUID uid, Member member){
        Optional<Member> optionalMember = memberRepository.findById(uid);
        if(optionalMember.isPresent()){
            try{
                Member currentMember = optionalMember.get();
                Member updateMember = newMember(member);
                updateMember.setId(currentMember.getId());
                updateMember.setPassword(currentMember.getPassword());
                memberRepository.save(updateMember);
                return updateMember;
            }catch (RuntimeException e){
                throw new RuntimeException(e);
            }
        }
        else{
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
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

    public List<Team> recommendTeams(UUID userId, int count, String token) {
        try {
            checkUserIdAndToken(token);
        }catch (AuthenticationException e){
            return null;
        }
        Optional<Member> optionalmember = memberRepository.findById(userId);
        if(optionalmember.isEmpty()){
            throw new RuntimeException("유저가 존재하지 않습니다.("+userId+")");
        }
        List<Team> teams = new ArrayList<>();
        List<Team> postTeams=springDataTeamRepository.findAll();
        for (Team postTeam : postTeams){
            teams.add(springDataTeamRepository.findById(postTeam.getTeamId()).get());
        }
        Member member = optionalmember.get();
        HashMap<Team, Integer> weight = new HashMap<>();
        for(Team lang : teams){
            Optional<Team> team = springDataTeamRepository.findById(lang.getTeamId());
            if(team.isPresent()) {
                weight.put(team.get(), lang.getC() * member.getC() + lang.getSqllang() * member.getSqllang() + lang.getCpp() * member.getCpp() + lang.getVb() * member.getVb() + lang.getCs() * member.getCs() + lang.getPhp() * member.getPhp() + lang.getPython() * member.getPython() + lang.getAssembly() * member.getAssembly() + lang.getJavascript() * member.getJavascript() + lang.getJava() * member.getJava());
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
            System.out.println("team.getTeamId() = " + team.getTeamId());
        }
        System.out.println("-------------------");
        return result;
    }


    public boolean checkEmailDuplicate(String email){
        return memberRepository.existsByEmail(email);
    }
    public boolean checkNicknameDuplicate(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
}
