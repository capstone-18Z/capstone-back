package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.controller.MemberData;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.Metadata;
import com.makedreamteam.capstoneback.form.PostResponseForm;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.form.Verification;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import javax.print.attribute.HashPrintJobAttributeSet;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final PostMemberRepository postMemberRepository;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private final FileService fileService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SolvedacService solvedacService;
    @Autowired
    private MemberKeywordRepository memberKeywordRepository;

    private static final Map<String, Verification> verifiedUserMap = new HashMap<>();

    private final String defaultprofileurl = "https://firebasestorage.googleapis.com/v0/b/caps-1edf8.appspot.com/o/DefaultProfile.PNG?alt=media&token=18e79bd3-f5b7-49c6-9edf-3939da9c2a84";

    public PostMember PostJoin(PostMember post, String authToken){
        if(authToken==null)
            throw new RuntimeException("로그인 상태가 아닙니다.");
        try {
            PostMember save = postMemberRepository.save(post);
            return save;
        } catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                 TransactionSystemException e) {
            throw new RuntimeException("Failed to add", e);
        }
    }

    public Member MemberJoin(Member post) {
        try {
            if (!checkEmailDuplicate(post.getEmail()) && !checkNicknameDuplicate(post.getNickname())) {
                if (verifiedUserMap.get(post.getEmail()) != null && verifiedUserMap.get(post.getEmail()).isVerified()) {
                    Member save = memberRepository.save(post);

                    MemberLang memberLang = new MemberLang();
                    memberLang.setMember(save);
                    save.setMemberLang(memberLang);

                    MemberFramework memberFramework = new MemberFramework();
                    memberFramework.setMember(save);
                    save.setMemberFramework(memberFramework);

                    MemberDatabase memberDatabase = new MemberDatabase();
                    memberDatabase.setMember(save);
                    save.setMemberDB(memberDatabase);

                    System.out.println("저장이 완료되었습니다!");
                    verifiedUserMap.remove(post.getEmail());
                    return save;
                } else {
                    throw new RuntimeException("이메일 인증을 해주세요.");
                }
            } else if (checkNicknameDuplicate(post.getNickname())) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            } else {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        } catch (NullPointerException | DataIntegrityViolationException | JpaSystemException |
                 TransactionSystemException e) {
            throw new RuntimeException("Failed to add", e);
        }
    }


    public void MemberUpdate(Member member, UUID uid, MultipartFile file) throws IOException {
        try {

            // 기존의 Member 엔티티 가져오기
            Member originalMember = memberRepository.findById(uid).get();
            /*List<MemberKeyword> keywords = member.getMemberKeywords();
            for (MemberKeyword tk : keywords) {
                tk.setMember(member);
            }*/
            List<MemberKeyword> keywords = member.getMemberKeywords();
            for (MemberKeyword tk : keywords) {
                tk.setMember(member);
            }

            // MemberLang, MemberFramework, MemberDatabase 엔티티에서 Member 엔티티를 참조하도록 설정
            if (member.getMemberLang() != null) {
                MemberLang memberLang = member.getMemberLang();
                memberLang.setMember(member);
                member.setMemberLang(memberLang);
            }
            if (member.getMemberFramework() != null) {
                MemberFramework memberFramework = member.getMemberFramework();
                memberFramework.setMember(member);
                member.setMemberFramework(memberFramework);
            }
            if (member.getMemberDB() != null) {
                MemberDatabase memberDatabase = member.getMemberDB();
                memberDatabase.setMember(member);
                member.setMemberDB(memberDatabase);
            }
            // id, email, nickname은 기존의 Member 엔티티에서 가져오기
            member.setId(originalMember.getId());
            member.setEmail(originalMember.getEmail());
            member.setPassword(originalMember.getPassword());
            member.setRole(originalMember.getRole());

            if(member.getSolvedNickname() != null && !member.getSolvedNickname().equals("")){
                SolvedAcUser solvedAcUser = solvedacService.getUser(member.getSolvedNickname());
                if(solvedAcUser.getError() == null) {
                    member.setSolvedTier(solvedAcUser.getTier());
                    member.setSolvedCount(solvedAcUser.getSolvedCount());
                    member.setSolvedProfile(solvedAcUser.getProfileImageUrl());
                }else{
                    member.setSolvedNickname("!!No User!!");
                }
            }

            // 프로필 이미지가 업데이트되는 경우, 기존 이미지 파일 삭제하고 새로운 파일 업로드하기
            if (file != null) {
                if (!originalMember.getProfileImageUrl().equals(defaultprofileurl)) {
                    fileService.deleteFile(originalMember.getProfileImageUrl());
                    System.out.println("파일 삭제 실행");
                }
                member.setProfileImageUrl(fileService.uploadProfile(file, uid).getImageURL());
                System.out.println("파일 저장실행");
            } else{
                member.setProfileImageUrl(originalMember.getProfileImageUrl());
            }
            // Member 엔티티 저장하기
            memberRepository.save(member);
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }
    }

    public void updateMemberPost(PostMember member, String authToken, String refreshToken) throws RefreshTokenExpiredException, AuthenticationException, LoginTokenExpiredException, TokenException, CannotFindTeamOrMember, DatabaseException {
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

        return MemberData.builder().Member(member).Token(Token.builder().accessToken(loginToken).refreshToken(refreshToken).build()).build();
    }

    public void sendVerificationEmail(String email) throws MessagingException {
        System.out.println("email : "+email);
        Verification verification = new Verification();
        String code = createCode();
        verification.setCode(code);
        verifiedUserMap.put(email, verification);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setSubject("이메일 인증을 완료해주세요.");
        String htmlMsg = "<h3>이메일 인증을 완료해주세요.</h3><br>"
                +"<p>아래 코드를 입력하여 이메일 인증을 완료해주세요.</p>"
                +"<p>"+code+"</p>";
        helper.setText(htmlMsg, true);
        javaMailSender.send(message);
    }

    public ResponseForm verifyEmail(String email,String code) {
        Verification verification = verifiedUserMap.get(email);
        if(verification.getCode().equals(code))
            verification.setVerified(true);
        else{
            throw new RuntimeException("코드가 일치하지 않습니다.");
        }
        verifiedUserMap.put(email, verification);
        return ResponseForm.builder().state(200).message("코드가 일치합니다").build();
    }

    public String createCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer code = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    public ResponseForm recommendTeams(String loginToken,String refreshToken){
        Map<Team,Double> recommendList=new HashMap<>();
        UUID userId=jwtTokenProvider.getUserId(loginToken);
        Member member=memberRepository.findById(userId).orElseThrow(()->{
            throw new RuntimeException("유저가 존재하지 않습니다");
        });
        long memberFrameworkId=member.getMemberFramework().getId();
        long memberDatabaseId=member.getMemberDB().getId();
        long startTime=System.currentTimeMillis();
        Pageable pageable= PageRequest.of(0,5);
        if (jwtTokenProvider.isValidAccessToken(loginToken)){
            List<UUID> teams=memberRepository.findTeamWithSameKeyword(userId);
            for(UUID teamId : teams){
                System.out.println("teamId : " +teamId);
            }
            List<Object[]> recommendTeamsByLanguage=memberRepository.recommendTeamWithLang(teams,userId,pageable);
            for(Object[] result : recommendTeamsByLanguage){

                Team team=(Team) result[0];
                double weight=(int) result[1]*1.1;
                System.out.println("lang Team : "+team.getTeamId());
                recommendList.put(team,weight);
            }
            List<Object[]> recommendTeamsByFramework=memberRepository.recommendTeamWithFramework(teams,userId,pageable);
            for(Object[] result : recommendTeamsByLanguage){
                Team team=(Team) result[0];
                double weight=(int) result[1];
                System.out.println("frame Team : "+team.getTeamId());
                recommendList.put(team,recommendList.get(team)+weight);
            }
            List<Object[]> recommendListByDatabase=memberRepository.recommendTeamWithDatabase(teams,userId,pageable);
            for(Object[] result : recommendTeamsByLanguage){
                Team team=(Team) result[0];
                double weight=(int) result[1];
                System.out.println("DB Team : "+team.getTeamId());
                recommendList.put(team,recommendList.get(team)+weight);
            }
            List<Team> list=new ArrayList<>(recommendList.keySet());
            Collections.sort(list, new Comparator<Team>() {
                @Override
                public int compare(Team t1, Team t2) {
                    double value1 = recommendList.get(t1);
                    double value2 = recommendList.get(t2);
                    return Double.compare(value2, value1); // 내림차순으로 정렬
                }
            });
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println("코드의 수행 시간(ms) : " + elapsedTime);

        for (Team team : list) {
            System.out.println("Team: " + team + ", Value: " + recommendList.get(team));
        }

            return ResponseForm.builder().message("추천 팀을 반환합니다").data(list).build();
        }else{
            return jwtTokenProvider.checkRefreshToken(refreshToken);
        }

    }

    public ResponseForm doFilteringMember(List<String> category, List<String> subject, List<String> rule, String search, int page) {
        int wantCount = 12;
        Pageable pageable = PageRequest.of(page-1, wantCount);
        if(category.size()==0 && rule.size()==0 && subject.size()==0){
            Page<Member> members = memberRepository.findMembersByNicknameContaining(search, pageable);
            int totalPage = members.getTotalPages();
            return ResponseForm.builder().message("멤버를 반환합니다").data(members.getContent()).metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).build();
        }

        if(search.equals("")) {
            Page<Member> members = null;

            if(category.isEmpty() && (rule.size()==1 && rule.get(0).equals("상관없음"))){
                System.out.println("category.isEmpty() && rule.size()==1 && rule.get(0).equalse(상관없음)");
                members=memberKeywordRepository.findAllByFilterWithoutCategoryAndRuleAndSearch(subject,pageable);
            } else if (category.isEmpty() && !(rule.size()==1 && rule.get(0).equals("상관없음"))) {
                System.out.println("category.isEmpty() && rule.size()>1");
                members=memberKeywordRepository.findAllByFilterWithoutCategoryAndSearch(subject,rule,pageable);
            } else if (!category.isEmpty() && (rule.size()==1 && rule.get(0).equals("상관없음"))) {
                System.out.println("!category.isEmpty() && rule.size()==1 && rule.get(0).equals(상관없음)");
                members=memberKeywordRepository.findAllByFilterWithoutRuleAndSearch(category,subject,pageable);
            }else{
                System.out.println("!category.isEmpty() && rule.size()>1");
                members=memberKeywordRepository.findAllByFilterWithoutSearch(category, subject, rule, pageable);
            }



            int totalPage = members.getTotalPages();
            return ResponseForm.builder().message("멤버를 반환합니다").data(members.getContent()).metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).build();
        }else{
            Page<Member> members = null;
            System.out.println("search : "+search);
            if(category.isEmpty() && (rule.size()==1 && rule.get(0).equals("상관없음"))){
                System.out.println("category.isEmpty() && rule.size()==1 && rule.get(0).equals(상관없음)");
                members=memberKeywordRepository.findAllByFilterWithoutCategoryAndRule(subject,search,pageable);
            } else if (category.isEmpty() && !(rule.size()==1 && rule.get(0).equals("상관없음"))) {
                System.out.println("category.isEmpty() && rule.size()>1");
                members=memberKeywordRepository.findAllByFilterWithoutCategory(subject,rule,search,pageable);
            } else if (!category.isEmpty() && (rule.size()==1 && rule.get(0).equals("상관없음"))) {
                System.out.println("!category.isEmpty() && rule.size()==1 && rule.get(0).equalse(상관없음)");
                members=memberKeywordRepository.findAllByFilterWithoutRule(category,subject,search,pageable);
            }else{
                System.out.println("!category.isEmpty() && rule.size()>1");
                members=memberKeywordRepository.findAllByFilter(category,subject,rule,search,pageable);
            }
            int totalPage = members.getTotalPages();
            return ResponseForm.builder().message("멤버를 반환합니다").data(members.getContent()).metadata(Metadata.builder().currentPage(page).totalPage(totalPage).build()).build();
        }
    }
}
