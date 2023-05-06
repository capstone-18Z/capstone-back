package com.makedreamteam.capstoneback.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.form.Metadata;
import com.makedreamteam.capstoneback.form.MyTeam;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Transactional
@Service
public class TeamService {
    @Autowired
    private final SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    private final TeamMemberRepository teamMemberRepository;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private Storage storage;

    @Autowired
    private final TeamKeywordRepository teamKeywordRepository;

    @Autowired
    TeamFrameworkRepository teamFrameworkRepository;

    @Autowired
    TeamDatabaseRepository teamDatabaseRepository;

    private JwtTokenProvider jwtTokenProvider;


    public TeamService(SpringDataTeamRepository springDataTeamRepository, TeamMemberRepository teamMemberRepository, RefreshTokenRepository refreshTokenRepository, TeamKeywordRepository teamKeywordRepository, JwtTokenProvider jwtTokenProvider) {
        this.springDataTeamRepository = springDataTeamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.teamKeywordRepository = teamKeywordRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseForm addNewTeam(Team team, List<MultipartFile> images, String authToken, String refreshToken) {
        if (jwtTokenProvider.isValidAccessToken(authToken)) {
            UUID teamLeader = UUID.fromString((String) jwtTokenProvider.getClaimsToken(authToken).get("userId"));
            List<Team> teamList = springDataTeamRepository.findByTeamLeader(teamLeader);
            if (teamList.size() == 100) {
                throw new RuntimeException("100개 이상의 팀을 만들 수 없습니다.");
            }
            try {
                if (images != null) {
                    List<String> imagesPath = uploadFile(images);
                    team.setImagePaths(imagesPath);
                }
                if(team.getCurrentTeamMemberCount()==0)
                    team.setCurrentTeamMemberCount((byte) 1);
                setTeamRelation(team);
                team.setTeamLeader(teamLeader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Team savedTeam = springDataTeamRepository.save(team);
            TeamMember teamMember = TeamMember.builder().teamId(savedTeam.getTeamId()).userId(savedTeam.getTeamLeader()).teamLeader(savedTeam.getTeamLeader()).build();
            teamMemberRepository.save(teamMember);
            System.out.println("유저 " + savedTeam.getTeamId() + "를 팀에 추가했습니다.");
            return ResponseForm.builder().state(HttpStatus.OK.value()).message("팀을 추가했습니다.").data(savedTeam).updatable(true).build();
        } else {
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm updateTest(Team team, List<MultipartFile> images, UUID teamId, String authToken, String refreshToken) {
        if (jwtTokenProvider.isValidAccessToken(authToken)) {
            try {
                UUID teamLeader = UUID.fromString((String) jwtTokenProvider.getClaimsToken(authToken).get("userId"));
                Team originalTeam = springDataTeamRepository.findById(teamId).orElseThrow(() -> null);
                if (images != null) {
                    if (originalTeam.getImagePaths() != null)
                        deleteFile(originalTeam.getImagePaths());
                    List<String> imagesPathFromClient = uploadFile(images);
                    team.setImagePaths(imagesPathFromClient);
                } else {
                    if (originalTeam.getImagePaths() != null)
                        deleteFile(originalTeam.getImagePaths());
                }
                setTeamRelation(team);
                team.setTeamId(teamId);
                team.setTeamLeader(teamLeader);
                Team updateResult = springDataTeamRepository.save(team);
                return ResponseForm.builder().message("팀을 업데이트 했습니다").state(HttpStatus.OK.value()).data(updateResult).updatable(true).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm deleteTest(UUID teamId, String authToken, String refreshToken) {
        if (jwtTokenProvider.isValidAccessToken(authToken)) {
            Team team = springDataTeamRepository.findById(teamId).orElseThrow(() -> {
                throw new RuntimeException("팀이 존재하지 않습니다.");
            });


            List<UUID> allByTeamId = teamMemberRepository.findAllByTeamId(teamId);
            teamMemberRepository.deleteAllById(allByTeamId);
            springDataTeamRepository.delete(team);
            return ResponseForm.builder().state(HttpStatus.OK.value()).message("팀을 삭제 했습니다.").build();
        } else {
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm findById(UUID teamId, String authToken, String refreshToken) {

        if (jwtTokenProvider.isValidAccessToken(authToken)) {//accesstoken이 유효하다면
            //팀 정보를db에서 가져온다
            Optional<Team> teambyId = springDataTeamRepository.findById(teamId);
            if (teambyId.isPresent()) {
                Claims claims = jwtTokenProvider.getClaimsToken(authToken);
                UUID userid = UUID.fromString((String) claims.get("userId"));
                //게시물을 만든 사용자와 게시물을 조회한 사용자의 ID를 비교
                if (teambyId.get().getTeamLeader().equals(userid)) {
                    //같다면 update를 true
                    return ResponseForm.builder().data(teambyId.get()).updatable(true).message("팀 조회").build();
                }
                //다르면 false
                return ResponseForm.builder().data(teambyId).updatable(false).message("팀 조회").build();
            }
            return ResponseForm.builder().message("팀이 존재하지 않습니다.").build();

        } else {//accesstoken이 만료되었다면
            return checkRefreshToken(refreshToken);
        }

    }

    public List<Team> allPosts(String loginToken, String refreshToken, int page) {
        try {
            Pageable pageable = PageRequest.of(page - 1, 20);
            List<Team> all = springDataTeamRepository.getAllTeamOrderByUpdateDesc(pageable);
            return all;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Failed to retrieve Team information from the database", e);
        }
    }

    public ResponseForm postListByTitle(String title, int page) {
        Pageable pageable = PageRequest.of(page - 1, 1);
        List<Team> teams = springDataTeamRepository.findTeamsByTitleContainingOrderByUpdateDateDesc(title, pageable).getContent();

        int totalPage = getTotalPageByTitle(title);
        if(teams.size()==0){
            return ResponseForm.builder().message(title+" 검색 결과가 존재하지 않습니다.").metadata(Metadata.builder().totalPage(0).currentPage(1).build()).build();
        }
        return ResponseForm.builder().state(HttpStatus.OK.value()).message("검색어 : "+title).data(teams).metadata(Metadata.builder().totalPage(totalPage).currentPage(page).build()).build();
    }

    public List<String> uploadFile(List<MultipartFile> files) throws IOException {
        List<String> images = new ArrayList<>();
        for (MultipartFile file : files) {
            Bucket bucket = StorageClient.getInstance().bucket("caps-1edf8.appspot.com");
            InputStream content = new ByteArrayInputStream(file.getBytes());
            Blob blob = bucket.create(System.currentTimeMillis() + "_" + file.getOriginalFilename(), content, file.getContentType());
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + blob.getName() + "?alt=media";
            images.add(imageUrl);
        }
        return images;
    }

    public void deleteFile(List<String> files) {
        for (String url : files) {
            String[] urlArr = url.split("/"); // "/"를 기준으로 문자열 분리
            String fileName = urlArr[urlArr.length - 1].split("\\?")[0];
            System.out.println("fileName = " + fileName);
            BlobId blobId = BlobId.of("caps-1edf8.appspot.com", fileName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                System.out.println("삭제됨");
            } else {
                System.out.println("삭제안됨");
            }
        }

    }


    public void setTeamRelation(Team team) {
        if (team.getTeamKeyword() != null) {
            TeamKeyword keyword = team.getTeamKeyword();
            keyword.setTeam(team);
        }
        //team language 양방향 설정
        if (team.getTeamLanguage() != null) {
            TeamLanguage teamLanguage = new TeamLanguage();
            teamLanguage = team.getTeamLanguage();
            teamLanguage.setTeam(team);
            team.setTeamLanguage(teamLanguage);
        }
        //team framework 양방향 설정
        if (team.getTeamFramework() != null) {
            TeamFramework teamFramework = new TeamFramework();
            teamFramework = team.getTeamFramework();
            teamFramework.setTeam(team);
            team.setTeamFramework(teamFramework);
        }
        //team database 양방향 설정
        if (team.getTeamDatabase() != null) {
            TeamDatabase teamDatabase = new TeamDatabase();
            teamDatabase = team.getTeamDatabase();
            teamDatabase.setTeam(team);
            team.setTeamDatabase(teamDatabase);
        }
    }

    public ResponseForm checkRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new NullPointerException("refreshTokenRepository.findById(team.getTeamLeader()) is empty");
        }
        if (jwtTokenProvider.isValidRefreshToken(refreshToken)) {//refreshtoken이 유효하다면
            //db에서 refreshtoken 검사
            Optional<RefreshToken> byRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
            if (byRefreshToken.isPresent()) {//db에 refresh토큰이 존재한다면
                //access토큰 재발급 요청
                return ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message("LonginToken 재발급이 필요합니다.").build();
            }
            //존재 하지않는다면
            return ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message("허용되지 않은 refreshtoken 입니다").build();
        } else {//refreshtoken이  만료되었다면
            return ResponseForm.builder().state(HttpStatus.BAD_REQUEST.value()).message("RefreshToken 이 만료되었습니다, 다시 로그인 해주세요").build();
        }
    }

    public ResponseForm recommendMembers(UUID teamId, String accessToken, String refreshToken) {

        Map<Member, Integer> result = new HashMap<>();
        Team team=springDataTeamRepository.findById(teamId).orElseThrow(()->{
            throw new RuntimeException("팀이 존재하지 않습니다");
        });
        long tfId=team.getTeamFramework().getId();
        long tdId=team.getTeamDatabase().getId();
        int limitFramework = teamFrameworkRepository.getTeamFrameworkTotalWeight(tfId);
        int limitDatabase = teamDatabaseRepository.getTeamDatabaseTotalWeight(tdId);
        long startTime2 = System.currentTimeMillis();

        Pageable pageable = PageRequest.of(0, 5);
        //team과 같은 키워드를 가진 member를 골라낸다
        //List<Member> memberAndTeamKeywordValues = springDataTeamRepository.findMemberAndTeamKeywordValues2(teamId);
        List<Member> memberAndTeamKeywordValues = springDataTeamRepository.findMemberAndTeamKeywordValues2(teamId);
        //List<Member> memberAndTeamKeywordValues=memberKeywordRepository.findSameKeywordToTeamKeyword(team.getTeamKeyword().getValue(),team.getTeamLeader());
        List<UUID> memberUUIDs = new ArrayList<>();
        for (Member member : memberAndTeamKeywordValues) {
            memberUUIDs.add(member.getId());
        }
        //1차로 언어 가중치를 이용해 1~5순위 member를 가려낸다
        List<Object[]> recommedMemberWithLang = springDataTeamRepository.recommendMemberWithLang(memberUUIDs, teamId, pageable);
        memberUUIDs.clear();
        for (Object[] member : recommedMemberWithLang) {
            Member m = (Member) member[0];
            int weight = (int) member[1]*2;
            memberUUIDs.add(m.getId());

            result.put(m, weight);
        }


        //가중치 도출해내고, 너무 작은 가중치의경우 리스트에서 제외시킨다
        List<Object[]> recommendMemberWithFramework = springDataTeamRepository.recommendMemberWithFramework(memberUUIDs, teamId, pageable);
        for (Object[] member : recommendMemberWithFramework) {
            Member m = (Member) member[0];
            int weight = (int) member[1];
            result.put(m, result.get(m) + weight);
        }
        List<Object[]> recommendMemberWithDatabase = springDataTeamRepository.recommendMemberWithDatabase(memberUUIDs, teamId, PageRequest.of(0, memberUUIDs.size() == 0 ? 1 : memberUUIDs.size()));
        for (Object[] member : recommendMemberWithDatabase) {
            Member m = (Member) member[0];
            int weight = (int) member[1];
            result.put(m, result.get(m) + weight);

        }
        List<Member> list = new ArrayList<>(result.keySet()); // KeySet을 리스트로 변환
        Collections.sort(list, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                Integer value1 = result.get(m1);
                Integer value2 = result.get(m2);
                return value2.compareTo(value1); // 내림차순으로 정렬
            }
        });
        long endTime2 = System.currentTimeMillis();
        long elapsedTime2 = endTime2 - startTime2;
        System.out.println("코드의 수행 시간(ms) : " + elapsedTime2);
        // 정렬된 리스트 출력
        for (Member member : list) {
            System.out.println("Member: " + member + ", Value: " + result.get(member));
        }


        return ResponseForm.builder().message("추천 유저를 반환합니다").data(list).build();
    }

    public long getTeamCount() {
        return springDataTeamRepository.count();
    }

    public int getTotalPage() {
        int pageSize = 20;
        int totalPage = (int) Math.ceil((double) springDataTeamRepository.getCountOfTeams() / pageSize);
        return totalPage;
    }

    public int getTotalPageByTitle(String title) {
        int pageSize = 1;
        return (int) Math.ceil((double) springDataTeamRepository.findTeamsByTitleContaining(title).size() / pageSize);
    }

    public ResponseForm getAllMyTeams(String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            UUID userId=jwtTokenProvider.getUserId(accessToken);

            List<Team> teamsByTeamLeader = springDataTeamRepository.findTeamsByTeamLeader(userId);
            List<MyTeam> myTeams=new ArrayList<>();
            for (Team team : teamsByTeamLeader){
                MyTeam myTeam=MyTeam.builder().teamId(team.getTeamId()).title(team.getTitle()).build();
                myTeams.add(myTeam);
            }
            return ResponseForm.builder().message("해당 유저의 팀을 반환합니다.").data(myTeams).build();
        }else{
            return checkRefreshToken(refreshToken);
        }
    }

    public ResponseForm doFilteringTeams(List<String> category, List<String> subject, List<String> rule,String search,int page) {
        Pageable pageable=PageRequest.of(page-1,20);
        if(category.size()==0 && rule.size()==0 && subject.size()==0){
            Page<Team> teamsByTitleContainingOrderByUpdateDateDesc = springDataTeamRepository.findTeamsByTitleContainingOrderByUpdateDateDesc(search, pageable);
            int totalPage=teamsByTitleContainingOrderByUpdateDateDesc.getTotalPages();
            return ResponseForm.builder().message("팀을 반환합니다").data(teamsByTitleContainingOrderByUpdateDateDesc.getContent()).metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).build();

        }

        if(search.equals("")) {
            Page<Team> teams = teamKeywordRepository.findAllByFilter(category, subject, rule, pageable);
            int totalPage = teams.getTotalPages();
            return ResponseForm.builder().message("팀을 반환합니다").data(teams.getContent()).metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).build();
        }else{
            System.out.println("search : "+search);
            Page<Team> teams = teamKeywordRepository.findAllByFilter(category, subject, rule, search, pageable);
            int totalPage = teams.getTotalPages();
            return ResponseForm.builder().message("팀을 반환합니다").data(teams.getContent()).metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).build();
        }
    }
}
