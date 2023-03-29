package com.makedreamteam.capstoneback.service;

import com.fasterxml.jackson.databind.introspect.MemberKey;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.LoginTokenExpiredException;
import com.makedreamteam.capstoneback.exception.RefreshTokenExpiredException;
import com.makedreamteam.capstoneback.exception.TokenException;
import com.makedreamteam.capstoneback.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
@Commit
class TeamServiceTest {

    @Autowired
    private SpringDataTeamRepository springDataTeamRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberKeywordRepository memberKeywordRepository;

    @Autowired
    private PostMemberRepository postMemberRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberService memberService;

    @org.junit.jupiter.api.Test
    void addPostTeam() {
        Team team= Team.builder().title("Asd").period(3).build();
        TeamKeyword teamKeyword = TeamKeyword.builder().value("good").team(team).build();
        TeamKeyword teamKeyword2 = TeamKeyword.builder().value("goood2").team(team).build();
        List<TeamKeyword> teamKeywordList =new ArrayList<>();
        teamKeywordList.add(teamKeyword);
        teamKeywordList.add(teamKeyword2);
        team.setTeamKeywords(teamKeywordList);

        springDataTeamRepository.save(team);
    }

    @Test
    public void addTeam(){
        String login="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiNWZjMmU4Zi0yNDBlLTQ0NjAtOWIxNC1mNDUyMmZmMGVjN2IiLCJlbWFpbCI6IjE4NzExNjZAbmF2ZXIuY29tIiwibmlja25hbWUiOiJydHkiLCJyb2xlcyI6IlJPTEVfTUVNQkVSIiwiaWF0IjoxNjc5ODY4NjkzLCJleHAiOjE2Nzk4NzA0OTN9.4Aeg0xSh1XISmVGarKY0PFBDXHYWlN8XoGZXx8v8k4M";
        String re="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiNWZjMmU4Zi0yNDBlLTQ0NjAtOWIxNC1mNDUyMmZmMGVjN2IiLCJlbWFpbCI6IjE4NzExNjZAbmF2ZXIuY29tIiwibmlja25hbWUiOiJydHkiLCJyb2xlcyI6IlJPTEVfTUVNQkVSIiwiaWF0IjoxNjc5ODY4NjkzLCJleHAiOjE2Nzk4ODY2OTN9.u8jkykocHe6xahfP3AluRpOXBDIXMOPBQTBTPMCB22M";
        Team team= Team.builder().title("asd").period(2).build();
        List<TeamKeyword> teamKeywordList =new ArrayList<>();
        for (int i=0;i<3;i++){
            teamKeywordList.add(TeamKeyword.builder().value("a"+i).team(team).build());

        }
        team.setTeamKeywords(teamKeywordList);

       // teamService.addPostTeam(team,login,re);
    }

    @Test
    public void c(){
        List<Team> all = springDataTeamRepository.findAll();
        for (Team team : all){
            for (TeamKeyword teamKeyword : team.getTeamKeywords()){
                System.out.println(teamKeyword.getValue());
            }
        }
    }

    @Test
    public void addMember() throws RefreshTokenExpiredException, AuthenticationException, LoginTokenExpiredException, TokenException {
//        String loginToken="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0MTk2NzgwOS03ODcwLTQxYjUtYWM5My0wYmUzMzA4MWI3NTciLCJlbWFpbCI6ImdnQGdnLmNvbSIsIm5pY2tuYW1lIjoibmljayIsInJvbGVzIjoiUk9MRV9NRU1CRVIiLCJpYXQiOjE2Nzk5MzQxODEsImV4cCI6MTY3OTkzNDE4Mn0.CWMUo4ZXYSe7Evul6709w7kOAUeL36p4U3D6NhIlj08";
//        String refreshToken="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0MTk2NzgwOS03ODcwLTQxYjUtYWM5My0wYmUzMzA4MWI3NTciLCJlbWFpbCI6ImdnQGdnLmNvbSIsIm5pY2tuYW1lIjoibmljayIsInJvbGVzIjoiUk9MRV9NRU1CRVIiLCJpYXQiOjE2Nzk5MzQxODEsImV4cCI6MTY3OTk1MjE4MX0.CowzfMQhCEpB34WlIan1uhlKbXumwilyLZU13k7xOjM";
//        List<MemberKeyword> memberKeywords=new ArrayList<>();
//       for (int i=0;i<3;i++){
//            memberKeywords.add(MemberKeyword.builder().value("a"+i).build());
//        }
//        Member member1= Member.builder().email("1@1.com").nickname("11").password("1234").build();
//        PostMember postMember= PostMember.builder().userId(UUID.fromString("41967809-7870-41b5-ac93-0be33081b757")).build();
//        for (MemberKeyword memberKeyword : memberKeywords){
//            memberKeyword.setPostMember(postMember);
//        }
//        postMember.setMemberKeywords(memberKeywords);
//
//        memberService.testAddNewMember(postMember,refreshToken,refreshToken);
    }

    @Test
    public void findMemberPost(){
        List<PostMember> postMembers=postMemberRepository.findAll();

        for(PostMember postMember : postMembers){
            List<MemberKeyword> memberKeyWords=postMember.getMemberKeywords();
            for (MemberKeyword memberKeyword : memberKeyWords){
                System.out.println(postMember.getPostId() +" : " + memberKeyword.getValue());
            }
        }
    }

    @Test
    public void recommend(){

        Team team = springDataTeamRepository.findById(UUID.fromString("93ce4380-8247-4a1f-bbad-8d0b2cec379b")).orElseThrow(() -> new RuntimeException("Team not found"));
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
                .collect(Collectors.toList());

        for (PostMember postMember : sortedPostMembers){
            System.out.println(postMember.getPostId());
        }
    }
}