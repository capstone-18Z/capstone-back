package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.form.ServiceReturn;
import com.makedreamteam.capstoneback.form.TeamData;
import com.makedreamteam.capstoneback.form.checkTokenResponsForm;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
public class TeamService{
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    private final TeamMemberRepository teamMemberRepository;
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;

    @Autowired
    private final KeywordRepository keywordRepository;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;


    private JwtTokenProvider jwtTokenProvider;


    public TeamService(SpringDataTeamRepository springDataTeamRepository, TeamMemberRepository teamMemberRepository, MemberRepository memberRepository, PostMemberRepository postMemberRepository, KeywordRepository keywordRepository, RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.springDataTeamRepository = springDataTeamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.memberRepository = memberRepository;
        this.postMemberRepository = postMemberRepository;
        this.keywordRepository = keywordRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //순서
    //로그인 된 userId를 팀리더로 team을 만든다
    //위에서 만들어진 teamId를 통해 temaLang을 만든다
    //temaId와 로그인된 userId, teamLeader로 teamMember를 만든다
    public ResponseForm addPostTeam(Team team, String authToken, String refreshToken) throws DatabaseException, TokenException {

        if (authToken == null)
            throw new RuntimeException("로그인 상태가 아닙니다.");
        //시나리오
        //accesstoken을 확인
        //accesstoken이 유효하면 게시물을 작성 유효하지 않다면 refresh토큰을 검사
        //refresh 토큰 검사 시 db에 저장되어있는지, 만료되었는지 검사
        //이후 accesstoke재발급필요 문구 전달

        try {
           // checkTokenResponsForm checkTokenResponsForm = checkUserIdAndToken(authToken, refreshToken);
            //임시
            if(jwtTokenProvider.isValidAccessToken(authToken)){//accesstoken 유효
                //addPost 진행
                System.out.println("accesstoken이 유효합니다 게시물을 추가합니다.");
                Claims userinfo= jwtTokenProvider.getClaimsToken(refreshToken);
                UUID teamLeader=UUID.fromString((String)userinfo.get("userId"));

                Optional<Member> byId = memberRepository.findById(teamLeader);
                if(byId.isEmpty()){
                    throw new RuntimeException("사용자가 존재하지 않습니다. 다시 로그인");
                }

                //String newToken = checkTokenResponsForm.getNewToken();
                List<Team> teams = springDataTeamRepository.findByTeamLeader(teamLeader);
                if (teams.size() == 3) {
                    throw new RuntimeException("4개 이상의 팀을 만들 수 없습니다.");
                }
                for (TeamKeyword teamKeyword : team.getTeamKeywords()){
                    teamKeyword.setTeam(team);
                }
                team.setTeamLeader(teamLeader);

                // 팀 저장
                Team savedTeam = springDataTeamRepository.save(team);
                UUID teamId = savedTeam.getTeamId();

                // 팀 멤버 저장
                TeamMember teamMember = TeamMember.builder().teamId(teamId).teamLeader(teamLeader).userId(teamLeader).build();
                TeamMember save = teamMemberRepository.save(teamMember);

                return ResponseForm.builder().state(HttpStatus.OK.value()).message("게시물을 등록했습니다.").data(TeamData.builder().team(savedTeam).build()).updatable(true).build();
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
    public ServiceReturn update(UUID teamId,Team updatedTeam,String authToken,String refreshToken) throws AuthenticationException, NotTeamLeaderException, RefreshTokenExpiredException, LoginTokenExpiredException {
        Optional<Team> optionalTeam = springDataTeamRepository.findById(teamId);
        if (optionalTeam.isEmpty()) {
            throw new RuntimeException("팀이 존재하지 않습니다");
        }

        try {
            checkTokenResponsForm checkTokenResponsForm = checkUserIdAndToken(authToken, refreshToken, optionalTeam);
            UUID userId = checkTokenResponsForm.getUserId();
            //String newToken=checkTokenResponsForm.getNewToken();
            Team team = optionalTeam.get();



            updatedTeam.setTeamId(team.getTeamId());
            updatedTeam.setTeamLeader(userId);
            springDataTeamRepository.save(updatedTeam);


            return ServiceReturn.builder().newToken(authToken).data(updatedTeam).build();
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }catch (NotTeamLeaderException e){
            throw new RuntimeException(e);
        } catch (RefreshTokenExpiredException e) {
            throw new RefreshTokenExpiredException(e.getMessage());
        } catch (LoginTokenExpiredException e) {
            throw new LoginTokenExpiredException(e.getMessage());
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
    public ResponseForm findById(UUID teamId,String authToken,String refreshToken) {

        if(jwtTokenProvider.isValidAccessToken(authToken)){//accesstoken이 유효하다면
            //팀 정보를db에서 가져온다
            Optional<Team> teambyId = springDataTeamRepository.findById(teamId);
            if(teambyId.isPresent()) {
                Claims claims = jwtTokenProvider.getClaimsToken(authToken);
                UUID userid = UUID.fromString((String) claims.get("userId"));
                //게시물을 만든 사용자와 게시물을 조회한 사용자의 ID를 비교
                if(teambyId.get().getTeamLeader().equals(userid)){
                    //같다면 update를 true
                    return ResponseForm.builder().data(TeamData.builder().team(teambyId.get()).build()).updatable(true).message("팀 조회").build();
                }
                //다르면 false
                return ResponseForm.builder().data(TeamData.builder().team(teambyId.get()).build()).updatable(false).message("팀 조회").build();
            }
            return ResponseForm.builder().message("팀이 존재하지 않습니다.").build();

        }else{//accesstoken이 만료되었다면
            if(jwtTokenProvider.isValidRefreshToken(refreshToken)){//refreshtoken이 유효하다면
                //db에서 refreshtoken 검사
                Optional<RefreshToken> byRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
                if(byRefreshToken.isPresent()){//db에 refresh토큰이 존재한다면
                    //access토큰 재발급 요청
                    return ResponseForm.builder().message("LonginToken 재발급이 필요합니다.").build();
                }
                //존재 하지않는다면
                return ResponseForm.builder().message("허용되지 않은 refreshtoken 입니다").build();
            }
            else{//refreshtoken이  만료되었다면
                return ResponseForm.builder().message("RefreshToken 이 만료되었습니다, 다시 로그인 해주세요").build();
            }

        }

    }
    public List<Team> allPosts(String loginToken, String refreshToken) {
        try {
            return springDataTeamRepository.findAll();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Failed to retrieve Team information from the database", e);
        }
    }
    public List<PostMember> recommendUsers(UUID teamId, int count) {
        //recommend는 위에서 토큰 인증을 진행했기때문에 따로 토큰의 유효성검사를 하지 않는다


            Optional<Team> optionalTeam=springDataTeamRepository.findById(teamId);
            if(optionalTeam.isEmpty()){
                throw new RuntimeException("팀이 존재하지 않습니다.("+teamId+")");
            }


            Team team=optionalTeam.get();
            Map<PostMember, Long> postMemberSimilarityMap = postMemberRepository.findAll().stream()
                    .collect(Collectors.toMap(Function.identity(),
                            postMember -> postMember.getMemberKeywords().stream()
                                    .filter(memberKeyword -> team.getTeamKeywords().stream()
                                            .anyMatch(teamKeyword -> teamKeyword.getValue().equals(memberKeyword.getValue())))
                                    .count()));

            // Map 객체를 유사도 기준으로 내림차순 정렬합니다.
            List<PostMember> sortedPostMembers = postMemberSimilarityMap.entrySet().stream()
                    .sorted(Map.Entry.<PostMember, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .limit(count)
                    .collect(Collectors.toList());
            return sortedPostMembers;


    }
    public ServiceReturn delete(UUID teamId,String authToken,String refreshToken) throws AuthenticationException, NotTeamLeaderException, RefreshTokenExpiredException, LoginTokenExpiredException {
        Optional<Team> teamOptional = springDataTeamRepository.findById(teamId);

        if(teamOptional.isEmpty())
            throw new EntityNotFoundException("fail to find team with "+teamId);

            checkTokenResponsForm checkTokenResponsForm = checkUserIdAndToken(authToken, refreshToken, teamOptional);
            Team team = teamOptional.get();
            List<TeamMember> teamMemberList = teamMemberRepository.findAllByTeamId(team.getTeamId());
            teamMemberRepository.deleteAll(teamMemberList);
            springDataTeamRepository.delete(team);
        return ServiceReturn.builder().build();
    }
    public checkTokenResponsForm checkUserIdAndToken(String token,String refreshToken, Optional<Team> team) throws AuthenticationException, NotTeamLeaderException, RefreshTokenExpiredException, LoginTokenExpiredException {

        System.out.println("in checkUserIdAndToken with team");
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
            System.out.println("UUID's userId = "+ claims.get("sub") +", Team userId = "+ team.get().getTeamLeader());
            if(team.isPresent())
                if(!UUID.fromString((String)claims.get("userId")).equals(team.get().getTeamLeader()) && !claims.get("roles").equals(Role.ROLE_ADMIN)) {
                    throw new NotTeamLeaderException("권한이 없습니다.");
                }


            return checkTokenResponsForm.builder().userId(UUID.fromString((String) claims.get("sub"))).build();
        } catch (ExpiredJwtException e) {
            System.out.println("만료");
            newToken=jwtTokenProvider.validateRefreshToken(refreshToken);
            if(newToken) {
//                System.out.println(newToken);
//                Jws<Claims> claimsJws = Jwts.parser().setSigningKey("test").parseClaimsJws(newToken);
//                Claims newClaims = claimsJws.getBody();
//                if(!UUID.fromString((String)newClaims.get("sub")).equals(team.get().getTeamLeader()) && !newClaims.get("roles").equals(Role.ROLE_ADMIN)) {
//                    throw new NotTeamLeaderException("권한이 없습니다.");
//                }
                //return checkTokenResponsForm.builder().userId(UUID.fromString((String) newClaims.get("sub"))).newToken(newToken).build();
                throw new LoginTokenExpiredException("새로운 토큰 발급이 필요합니다");
            }else
                throw new RefreshTokenExpiredException("토큰이 만료되었습니다");
        } catch (JwtException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }
    public checkTokenResponsForm checkUserIdAndToken(String token,String refreshToken) throws AuthenticationException, RefreshTokenExpiredException, TokenException, LoginTokenExpiredException {
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



}
