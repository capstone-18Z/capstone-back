package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.controller.MemberData;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.PostResponseForm;
import com.makedreamteam.capstoneback.form.Verification;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
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
    private final SpringDataTeamRepository springDataTeamRepository;

    @Autowired
    private final FileDataRepository fileDataRepository;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private final FileService fileService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private final MemberLangRepository memberLangRepository;
    @Autowired
    private final MemberFrameworkRepository memberFrameworkRepository;
    @Autowired
    private final MemberDatabaseRepository memberDatabaseRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Map<String, Verification> verifiedUserMap = new HashMap<>();

    public PostMember PostJoin(PostMember post, String authToken) {
        if (authToken == null)
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
            String defaultProfile = "https://firebasestorage.googleapis.com/v0/b/caps-1edf8.appspot.com/o/DefaultProfile.PNG?alt=media&token=266e52f4-818f-4a20-970d-2d84ba48e5a1";

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

            // 프로필 이미지가 업데이트되는 경우, 기존 이미지 파일 삭제하고 새로운 파일 업로드하기
            if (file != null) {
                if (!originalMember.getProfileImageUrl().equals(defaultProfile)) {
                    fileService.deleteFile(originalMember.getProfileImageUrl());
                    System.out.println("파일 삭제 실행");
                }
                member.setProfileImageUrl(fileService.uploadProfile(file, uid).getImageURL());
                System.out.println("파일 저장실행");
            }
            // Member 엔티티 저장하기
            memberRepository.save(member);
        } catch (RuntimeException e) {
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
        } catch (DataIntegrityViolationException | JpaSystemException | TransactionSystemException e) {
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.");
        } catch (JwtException ex) {
            throw new TokenException(ex.getMessage());
        }

    }

    public void deletePost(Long postid, String authToken, String refreshToken) throws RefreshTokenExpiredException, TokenException, DatabaseException {
        try {
            // checkTokenResponsForm checkTokenResponsForm = checkUserIdAndToken(authToken, refreshToken);
            if (jwtTokenProvider.isValidAccessToken(authToken)) {//accesstoken 유효
                //addPost 진행
                System.out.println("accesstoken이 유효합니다");
                Claims userinfo = jwtTokenProvider.getClaimsToken(refreshToken);
                UUID writer = UUID.fromString((String) userinfo.get("userId"));
                Optional<Member> byId = memberRepository.findById(writer);
                PostMember postMember = postMemberRepository.findByPostId(postid).get();
                if (byId.isEmpty()) {
                    throw new RuntimeException("사용자가 존재하지 않습니다.");
                }
                List<FileData> fileList = postMember.getFileDataList();
                for (FileData file : fileList) {
                    fileService.deleteFile(file);
                }
                //String newToken = checkTokenResponsForm.getNewToken();
                System.out.println("postid : " + postid + "를 삭제합니다.");
                postMemberRepository.deleteById(postid);
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

        } catch (DataIntegrityViolationException | JpaSystemException | TransactionSystemException e) {
            throw new DatabaseException("데이터베이스 처리 중 오류가 발생했습니다.");
        } catch (JwtException ex) {
            throw new TokenException(ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID checkUserIdAndToken(String authToken, String refreshToken) throws AuthenticationException {
        if (jwtTokenProvider.isValidAccessToken(authToken)) {//accesstoken 유효
            //addPost 진행
            System.out.println("accesstoken이 유효합니다");
            Claims userinfo = jwtTokenProvider.getClaimsToken(refreshToken);
            UUID uid = UUID.fromString((String) userinfo.get("userId"));
            return uid;
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
                    throw new AuthenticationException("accseetoken 재발급이 필요합니다.");
                } else {
                    //db에 없는 토큰이므로 오류메시지 출력
                    System.out.println("허용되지 않은 refreshtoken 입니다");
                    throw new AuthenticationException("허용되지 않은 refreshtoken 입니다");
                }
            } else {
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
            if (jwtTokenProvider.isValidAccessToken(authToken)) {//accesstoken 유효
                //addPost 진행
                System.out.println("accesstoken이 유효합니다 게시물을 추가합니다.");
                Claims userinfo = jwtTokenProvider.getClaimsToken(refreshToken);
                UUID writer = UUID.fromString((String) userinfo.get("userId"));

                Optional<Member> byId = memberRepository.findById(writer);
                if (byId.isEmpty()) {
                    throw new RuntimeException("사용자가 존재하지 않습니다.");
                }

                //String newToken = checkTokenResponsForm.getNewToken();
                member.setMember(byId.get());
                member.setNickname(byId.get().getNickname());
                // post 저장
                PostMember saved = postMemberRepository.save(member);

                return PostResponseForm.builder().state(HttpStatus.OK.value()).message("게시물을 등록했습니다.").data(saved).pid(saved.getPostId()).updatable(true).build();
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
                        return PostResponseForm.builder().message("LonginToken 재발급이 필요합니다.").build();
                    } else {
                        //db에 없는 토큰이므로 오류메시지 출력
                        System.out.println("허용되지 않은 refreshtoken 입니다");
                        return PostResponseForm.builder().message("허용되지 않은 RefreshToken 입니다").build();
                    }
                } else {
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


    public boolean checkEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public MemberData doLogin(Member member) {
        //새로운 토큰 생성
        String loginToken = jwtTokenProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole(),
                member.getNickname());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        RefreshToken refreshTok = RefreshToken.builder().userId(member.getId()).refreshToken(refreshToken).build();

        //db에 해당 유저의 refresh토큰이 존재한다면 업데이트한다
        Optional<RefreshToken> memberRefreshToken = refreshTokenRepository.findById(member.getId());
        if (memberRefreshToken.isPresent()) {
            RefreshToken savedRefreshToken = memberRefreshToken.get();
            savedRefreshToken.setRefreshToken(refreshToken);
            refreshTokenRepository.save(savedRefreshToken);
        } else refreshTokenRepository.save(refreshTok);

        return MemberData.builder().Member(member).Token(Token.builder().accessToken(loginToken).refreshToken(refreshToken).build()).build();
    }

    public void sendVerificationEmail(String email) throws MessagingException {
        Verification verification = new Verification();
        String code = createCode();
        verification.setCode(code);
        verifiedUserMap.put(email, verification);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email+"@hansung.ac.kr");
        helper.setSubject("이메일 인증을 완료해주세요.");
        String htmlMsg = "<h3>이메일 인증을 완료해주세요.</h3><br>"
                +"<p>아래 코드를 입력하여 이메일 인증을 완료해주세요.</p>"
                +"<p>"+code+"</p>";
        helper.setText(htmlMsg, true);
        javaMailSender.send(message);
    }

    public void verifyEmail(String email,String code) {
        Verification verification = verifiedUserMap.get(email);
        if(verification.getCode().equals(code))
            verification.setVerified(true);
        else{
            throw new RuntimeException("코드가 일치하지 않습니다.");
        }
        verifiedUserMap.put(email, verification);
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
}
