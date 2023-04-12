package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.controller.MemberData;
import com.makedreamteam.capstoneback.controller.MemberResponseForm;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.PostResponseForm;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.form.TeamData;
import com.makedreamteam.capstoneback.form.checkTokenResponsForm;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;
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
    private final FileDataRepository fileDataRepository;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private final FileService fileService;

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

    public void updateMemberPost(PostMember member,String authToken,String refreshToken) throws RefreshTokenExpiredException, AuthenticationException, LoginTokenExpiredException, TokenException, CannotFindTeamOrMember, DatabaseException {
        try {
            if (jwtTokenProvider.isValidAccessToken(authToken)) {
                System.out.println("accesstoken이 유효합니다");
                postMemberRepository.save(member);
            } else {//accesstoken 만료
                if (jwtTokenProvider.isValidRefreshToken(refreshToken)) {//refreshtoken 유효성검사
                    //refreshtoken db 검사
                    System.out.println("accesstoken이 만료되었습니다");
                    System.out.println("refreshtoken 유효성 검사를 시작합니다");
                    Claims userinfo = jwtTokenProvider.getClaimsToken(refreshToken);
                    UUID userId = UUID.fromString((String) userinfo.get("userId"));
                    Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(userId);
                    if (optionalRefreshToken.isPresent()) {
                        //db에 존재하므로 access토큰 재발급 문자 출력
                        System.out.println("accseetoken 재발급이 필요합니다.");
                    } else {
                        //db에 없는 토큰이므로 오류메시지 출력
                        System.out.println("허용되지 않은 refreshtoken 입니다");
                    }
                } else {
                    // 다시 login 시도
                    System.out.println("refreshtoken이 만료되었습니다, 다시 로그인 해주세요");
                }
            }
        }catch (DataIntegrityViolationException | JpaSystemException | TransactionSystemException e) {
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.");
        } catch (JwtException ex) {
            throw new TokenException(ex.getMessage());
        }

    }

    public void deletePost(Long postid, String authToken, String refreshToken) throws RefreshTokenExpiredException, TokenException, DatabaseException{
        try {
            // checkTokenResponsForm checkTokenResponsForm = checkUserIdAndToken(authToken, refreshToken);
            if(jwtTokenProvider.isValidAccessToken(authToken)){//accesstoken 유효
                //addPost 진행
                System.out.println("accesstoken이 유효합니다");
                Claims userinfo= jwtTokenProvider.getClaimsToken(refreshToken);
                UUID writer=UUID.fromString((String)userinfo.get("userId"));
                Optional<Member> byId = memberRepository.findById(writer);
                PostMember postMember = postMemberRepository.findByPostId(postid).get();
                if(byId.isEmpty()){
                    throw new RuntimeException("사용자가 존재하지 않습니다.");
                }
                List<FileData> fileList = postMember.getFileDataList();
                for (FileData file : fileList) {
                    fileService.deleteFile(file);
                }
                //String newToken = checkTokenResponsForm.getNewToken();
                System.out.println("postid : "+postid+"를 삭제합니다.");
                postMemberRepository.deleteById(postid);
            }
            else{//accesstoken 만료
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
                    }
                    else{
                        //db에 없는 토큰이므로 오류메시지 출력
                        System.out.println("허용되지 않은 refreshtoken 입니다");
                    }
                }
                else{
                    // 다시 login 시도
                    System.out.println("refreshtoken이 만료되었습니다, 다시 로그인 해주세요");
                }
            }

        } catch (DataIntegrityViolationException | JpaSystemException | TransactionSystemException e) {
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.");
        } catch (JwtException ex) {
            throw new TokenException(ex.getMessage());
        } 
    }



    public UUID checkUserIdAndToken(String authToken, String refreshToken) throws AuthenticationException {
        if(jwtTokenProvider.isValidAccessToken(authToken)) {//accesstoken 유효
            //addPost 진행
            System.out.println("accesstoken이 유효합니다");
            Claims userinfo = jwtTokenProvider.getClaimsToken(refreshToken);
            UUID uid = UUID.fromString((String) userinfo.get("userId"));
            return uid;
        }
        else{//accesstoken 만료
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
                    throw new AuthenticationException("accseetoken 재발급이 필요합니다.");
                }
                else{
                    //db에 없는 토큰이므로 오류메시지 출력
                    System.out.println("허용되지 않은 refreshtoken 입니다");
                    throw new AuthenticationException("허용되지 않은 refreshtoken 입니다");
                }
            }
            else{
                // 다시 login 시도
                System.out.println("refreshtoken이 만료되었습니다, 다시 로그인 해주세요");
                throw new AuthenticationException("refreshtoken이 만료되었습니다, 다시 로그인 해주세요");
            }
        }
    }

    public List<Team> recommendTeams(UUID userId, int count) {

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

    public List<Team> recommendTeamsByKeyword(Long postid, int count) {
        //recommend는 위에서 토큰 인증을 진행했기때문에 따로 토큰의 유효성검사를 하지 않는다
        Optional<PostMember> optionalPostMember=postMemberRepository.findById(postid);
        if(optionalPostMember.isEmpty()){
            throw new RuntimeException("팀이 존재하지 않습니다.("+postid+")");
        }

        PostMember postMember = optionalPostMember.get();
        Map<Team, Long> teamSimilarityMap = springDataTeamRepository.findAll().stream()
                .collect(Collectors.toMap(Function.identity(),
                        team -> team.getTeamKeywords().stream()
                                .filter(teamKeyword -> postMember.getMemberKeywords().stream()
                                        .anyMatch(memberKeyword -> memberKeyword.getValue().equals(teamKeyword.getValue())))
                                .count()));

        // Map 객체를 유사도 기준으로 내림차순 정렬합니다.
        List<Team> sortedTeams = teamSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<Team, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(count)
                .collect(Collectors.toList());
        return sortedTeams;
    }

    public PostResponseForm testAddNewMember(PostMember member, String authToken, String refreshToken) throws RefreshTokenExpiredException, TokenException, DatabaseException {

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
                List<MemberKeyword> memberKeywords=member.getMemberKeywords();
                if(memberKeywords!=null)
                    for (MemberKeyword memberKeyword : memberKeywords){
                        memberKeyword.setPostMember(member);
                    }
                member.setMember(byId.get());
                member.setNickname(byId.get().getNickname());
                // post 저장
                PostMember saved=postMemberRepository.save(member);

                return PostResponseForm.builder().state(HttpStatus.OK.value()).message("게시물을 등록했습니다.").data(saved).pid(saved.getPostId()).updatable(true).build();
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
                        return PostResponseForm.builder().message("LonginToken 재발급이 필요합니다.").build();
                    }
                    else{
                        //db에 없는 토큰이므로 오류메시지 출력
                        System.out.println("허용되지 않은 refreshtoken 입니다");
                        return PostResponseForm.builder().message("허용되지 않은 RefreshToken 입니다").build();
                    }
                }
                else{
                    // 다시 login 시도
                    System.out.println("refreshtoken이 만료되었습니다, 다시 로그인 해주세요");
                    return PostResponseForm.builder().message("RefreshToken 이 만료되었습니다, 다시 로그인 해주세요").build();
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
