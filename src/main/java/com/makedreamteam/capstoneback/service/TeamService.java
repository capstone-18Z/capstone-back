package com.makedreamteam.capstoneback.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
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
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private Storage storage;

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
                Claims userinfo= jwtTokenProvider.getClaimsToken(authToken);
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
                List<TeamKeyword> teamKeywords=team.getTeamKeywords();
                if(teamKeywords!=null)
                    for (TeamKeyword teamKeyword : teamKeywords){
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
               return checkRefreshToken(refreshToken);
            }

        } catch (DataIntegrityViolationException | JpaSystemException | TransactionSystemException e) {
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.");
        } catch (JwtException ex) {
            throw new TokenException(ex.getMessage());
        }

    }
    public ResponseForm update(UUID teamId,Team team,List<MultipartFile> images,String accessToken,String refreshToken) throws IOException {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            Optional<Team> optionalTeam = springDataTeamRepository.findById(teamId);
            if(optionalTeam.isEmpty()){
                throw new RuntimeException("springDataTeamRepository.findById(team.getTeamId()) is empty");
            }
            Team updatedTeam=optionalTeam.get();

            //이미지를 삭제한다
            deleteFile(updatedTeam.getImagePaths());
            List<String> imageURL=uploadFile(images);
            for(TeamKeyword teamKeyword : team.getTeamKeywords()){
                teamKeyword.setTeam(team);
            }
            team.setTeamId(updatedTeam.getTeamId());
            team.setImagePaths(imageURL);
            team.setTeamLeader(updatedTeam.getTeamLeader());
            Team savedTeam = springDataTeamRepository.save(team);
            return ResponseForm.builder().data(TeamData.builder().team(savedTeam).build()).build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }


    public ResponseForm findByTitleContaining(String title){
        return null;
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
           return checkRefreshToken(refreshToken);
        }

    }
    public List<Team> allPosts(String loginToken, String refreshToken) {
        try {
            return springDataTeamRepository.findAllByOrderByUpdateDateDesc();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Failed to retrieve Team information from the database", e);
        }
    }
    public List<Member> recommendUsers(UUID teamId, int count) {
        //recommend는 위에서 토큰 인증을 진행했기때문에 따로 토큰의 유효성검사를 하지 않는다


            Optional<Team> optionalTeam=springDataTeamRepository.findById(teamId);
            if(optionalTeam.isEmpty()){
                throw new RuntimeException("팀이 존재하지 않습니다.("+teamId+")");
            }


            Team team=optionalTeam.get();
            Map<Member, Long> memberSimilarityMap = memberRepository.findAll().stream()
                    .collect(Collectors.toMap(Function.identity(),
                            member -> member.getMemberKeywords().stream()
                                    .filter(memberKeyword -> team.getTeamKeywords().stream()
                                            .anyMatch(teamKeyword -> teamKeyword.getValue().equals(memberKeyword.getValue())))
                                    .count()));

            // Map 객체를 유사도 기준으로 내림차순 정렬합니다.
            List<Member> sortedMembers = memberSimilarityMap.entrySet().stream()
                    .sorted(Map.Entry.<Member, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .limit(count)
                    .collect(Collectors.toList());
            return sortedMembers;


    }
    public ResponseForm delete(UUID teamId,String authToken,String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(authToken)){
            Optional<Team> optionalTeam = springDataTeamRepository.findById(teamId);
            if(optionalTeam.isEmpty()){
                throw new RuntimeException("springDataTeamRepository.findById(team.getTeamId()) is empty");
            }
            Team team=optionalTeam.get();
            springDataTeamRepository.delete(team);

            return ResponseForm.builder().message("삭제를 완료했습니다.").build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }
    public ResponseForm checkRefreshToken(String refreshToken){
        if(refreshToken==null){
            throw new NullPointerException("refreshTokenRepository.findById(team.getTeamLeader()) is empty");
        }
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

    public void addNewTeamWithImage(MultipartFile[] imageFiles, Team team, String refreshToken, String accessToken) throws Exception {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            List<String> imagePaths = new ArrayList<>();

            for (MultipartFile imageFile : imageFiles) {
                String imagePath = saveImageToLocal(imageFile);
                imagePaths.add(imagePath);
            }
            team.setImagePaths(imagePaths);
            springDataTeamRepository.save(team);
        }else {
            checkRefreshToken(refreshToken);
        }
    }
    private String saveImageToLocal(MultipartFile imageFile) throws Exception {
        String fileName = imageFile.getOriginalFilename();
        String filePath = "C:/images/" + fileName;
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        imageFile.transferTo(file);
        return filePath;
    }

    public List<String> uploadFile(List<MultipartFile> files) throws IOException {
        List<String> images=new ArrayList<>();
        for (MultipartFile file : files){
            Bucket bucket= StorageClient.getInstance().bucket("caps-1edf8.appspot.com");
            InputStream content =new ByteArrayInputStream(file.getBytes());
            Blob blob=bucket.create(System.currentTimeMillis() + "_" +file.getOriginalFilename(),content,file.getContentType());
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName()+"?alt=media";
            images.add(imageUrl);
        }
        return images;
    }
    public void deleteFile(List<String> files){
        for(String url : files){
            String[] urlArr = url.split("/"); // "/"를 기준으로 문자열 분리
            String fileName = urlArr[urlArr.length - 1].split("\\?")[0];
            System.out.println("fileName = "+fileName);
            BlobId blobId = BlobId.of("caps-1edf8.appspot.com", fileName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                System.out.println("삭제됨");
            } else {
                System.out.println("삭제안됨");
            }
        }

    }
}
