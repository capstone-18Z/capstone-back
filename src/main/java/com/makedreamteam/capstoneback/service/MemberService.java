package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.controller.MemberData;
import com.makedreamteam.capstoneback.controller.MemberResponseForm;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.form.TeamData;
import com.makedreamteam.capstoneback.form.checkTokenResponsForm;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


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
               // weight.put(team.get(), lang.getC() * member.getC() + lang.getSqllang() * member.getSqllang() + lang.getCpp() * member.getCpp() + lang.getVb() * member.getVb() + lang.getCs() * member.getCs() + lang.getPhp() * member.getPhp() + lang.getPython() * member.getPython() + lang.getAssembly() * member.getAssembly() + lang.getJavascript() * member.getJavascript() + lang.getJava() * member.getJava());
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

    public void updateMemberPost(PostMember member,String authToken,String refreshToken) throws RefreshTokenExpiredException, AuthenticationException, LoginTokenExpiredException, TokenException, CannotFindTeamOrMember {

        checkTokenResponsForm check = checkUserIdAndToken(authToken, refreshToken);

        Optional<Member> optionalMember = memberRepository.findById(member.getUserId());
        if(optionalMember.isEmpty()){
            throw new CannotFindTeamOrMember("사용자를 찾을수 없습니다");
        }
        List<MemberKeyword> memberKeywords=member.getMemberKeywords();
        for (MemberKeyword memberKeyword : memberKeywords){
            memberKeyword.setPostMember(member);
        }
        member.setMemberKeywords(memberKeywords);
        postMemberRepository.save(member);

    }

    public ResponseForm testAddNewMember(PostMember member, String authToken, String refreshToken) throws RefreshTokenExpiredException, TokenException, DatabaseException {

        try {
            // checkTokenResponsForm checkTokenResponsForm = checkUserIdAndToken(authToken, refreshToken);
            //임시
            if(jwtTokenProvider.isValidAccessToken(authToken)){//accesstoken 유효
                //addPost 진행
                System.out.println("accesstoken이 유효합니다 게시물을 추가합니다.");
                Claims userinfo= jwtTokenProvider.getClaimsToken(refreshToken);
                UUID writer=UUID.fromString((String)userinfo.get("userId"));

                Optional<Member> byId = memberRepository.findById(writer);
                if(byId.isEmpty()){
                    throw new RuntimeException("사용자가 존재하지 않습니다.");
                }

                //String newToken = checkTokenResponsForm.getNewToken();

                for (MemberKeyword memberKeyword : member.getMemberKeywords()){
                    memberKeyword.setPostMember(member);
                }
                member.setUserId(writer);

                // post 저장
                PostMember saved=postMemberRepository.save(member);




                return ResponseForm.builder().state(HttpStatus.OK.value()).message("게시물을 등록했습니다.").data(saved).updatable(true).build();
            }else{//accesstoken 만료
                if(jwtTokenProvider.isValidRefreshToken(refreshToken)){//refreshtoken 유효성검사
                    //refreshtoken db 검사
                    System.out.println("accesstoken이 만료되었습니다");
                    System.out.println("refreshtoken 유효성 검사를 시작합니다");
                    Claims userinfo= jwtTokenProvider.getClaimsToken(refreshToken);
                    UUID userId=UUID.fromString((String)userinfo.get("userId"));
                    Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(userId);
                    if(optionalRefreshToken.isPresent()){
                        //db에 존재하므로 access토큰 재발급 문자 출력
                        System.out.println("accseetoken 재발급이 필요합니다.");
                        return ResponseForm.builder().message("LonginToken 재발급이 필요합니다.").build();
                    }
                    else{
                        //db에 없는 토큰이므로 오류메시지 출력
                        System.out.println("허용되지 않은 refreshtoken 입니다");
                        return ResponseForm.builder().message("허용되지 않은 RefreshToken 입니다").build();
                    }
                }
                else{
                    // 다시 login 시도
                    System.out.println("refreshtoken이 만료되었습니다, 다시 로그인 해주세요");
                    return ResponseForm.builder().message("RefreshToken 이 만료되었습니다, 다시 로그인 해주세요").build();
                }
            }

        } catch (DataIntegrityViolationException | JpaSystemException | TransactionSystemException e) {
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.");
        } catch (JwtException ex) {
            throw new TokenException(ex.getMessage());
        }
    }


    public boolean checkEmailDuplicate(String email){
        return memberRepository.existsByEmail(email);
    }
    public boolean checkNicknameDuplicate(String nickname){
        return memberRepository.existsByNickname(nickname);
    }

    public checkTokenResponsForm checkUserIdAndToken(String token, String refreshToken) throws AuthenticationException, RefreshTokenExpiredException, TokenException, LoginTokenExpiredException {
        if (token == null) {
            throw new AuthenticationException("Invalid Authorization header");
        }

        boolean newToken = false;

        Claims claims = null;
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey("test").parseClaimsJws(token);
            claims = claimsJws.getBody();
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();

            if (username == null | expirationDate==null) {
                throw new AuthenticationException("Invalid JWT claims");
            }
            return checkTokenResponsForm.builder().userId(UUID.fromString((String) claims.get("sub"))).build();
        } catch (ExpiredJwtException e) {
            try {

                newToken = jwtTokenProvider.validateRefreshToken(refreshToken);
                if (newToken) {
//                    System.out.println(newToken);
//                    Jws<Claims> claimsJws = Jwts.parser().setSigningKey("test").parseClaimsJws(newToken);
//                    Claims newClaims = claimsJws.getBody();
//                    return checkTokenResponsForm.builder().userId(UUID.fromString((String) newClaims.get("sub"))).newToken(newToken).build();
                    throw new LoginTokenExpiredException("새로운 토큰 발급이 필요합니다");
                } else
                    throw new RefreshTokenExpiredException("토큰이 만료되었습니다");
            }catch (RefreshTokenExpiredException m){
                throw new RefreshTokenExpiredException(m.getMessage());
            } catch (LoginTokenExpiredException ex) {
                throw new LoginTokenExpiredException(ex.getMessage());
            }
        } catch (JwtException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    public MemberData doLogin(Member member) {



        //새로운 토큰 생성
        String loginToken= jwtTokenProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole(),
                member.getNickname());
        String refreshToken= jwtTokenProvider.createRefreshToken(member.getId());
        RefreshToken refreshTok= RefreshToken.builder().userId(member.getId()).refreshToken(refreshToken).build();

        //db에 해당 유저의 refresh토큰이 존재한다면 업데이트한다
        Optional<RefreshToken> memberRefreshToken = refreshTokenRepository.findById(member.getId());
        if(memberRefreshToken.isPresent()){
            RefreshToken savedRefreshToken=memberRefreshToken.get();
            savedRefreshToken.setRefreshToken(refreshToken);
            refreshTokenRepository.save(savedRefreshToken);
        }
        else refreshTokenRepository.save(refreshTok);

        return MemberData.builder().Token(Token.builder().accessToken(loginToken).refreshToken(refreshToken).build()).build();
    }
}
