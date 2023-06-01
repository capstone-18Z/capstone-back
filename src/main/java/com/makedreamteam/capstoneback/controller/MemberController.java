package com.makedreamteam.capstoneback.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.MemberResponseForm;
import com.makedreamteam.capstoneback.form.PostResponseForm;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.CommentRepository;
import com.makedreamteam.capstoneback.repository.FileDataRepository;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.service.*;
import com.makedreamteam.capstoneback.service.ContestCrawlingService;
import com.makedreamteam.capstoneback.service.FileService;
import com.makedreamteam.capstoneback.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import javax.naming.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ContestCrawlingService contestCrawlingService;
    private final SolvedacService solvedacService;

    private final String defaultprofileurl = "https://firebasestorage.googleapis.com/v0/b/caps-1edf8.appspot.com/o/DefaultProfile.PNG?alt=media&token=18e79bd3-f5b7-49c6-9edf-3939da9c2a84";

    // 회원가입
    @PostMapping("/register")
    public Member register(@RequestBody Map<String, String> user) throws MessagingException {
        Member member = Member.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .nickname(user.get("nickname"))
                .profileImageUrl(defaultprofileurl)
                .role(Role.ROLE_MEMBER)
                .build();
        return memberService.MemberJoin(member);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberResponseForm> login(@RequestBody Map<String, String> user) {
        try {
            Member member = memberRepository.findByEmail(user.get("email"))
                    .orElseThrow(() -> new IllegalArgumentException("가입 되지 않은 이메일입니다."));
            if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
                throw new IllegalArgumentException("이메일 또는 비밀번호가 맞지 않습니다.");
            }
            MemberData memberData=memberService.doLogin(member);
            MemberResponseForm memberResponseForm = MemberResponseForm.builder()
                    .data(memberData)
                    .state(HttpStatus.OK.value())
                    .message("로그인 성공")
                    .build();
            return ResponseEntity.ok().body(memberResponseForm);
        } catch (RuntimeException e){
            MemberResponseForm errorResponse = MemberResponseForm.builder()
                    .message("Failed to Login : " + e.getMessage())
                    .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/search/email/{email}")
    public Optional<Member> SearchMemberbyEmail(@PathVariable String email){
        return memberRepository.findByEmail(email);
    }

    @GetMapping("/search/nickname/{nickname}")
    public Optional<Member> SearchMemberbyNickname(@PathVariable String nickname){
        return memberRepository.findByNickname(nickname);
    }

    @GetMapping("/search/uid/{uid}")
    public Optional<Member> SearchMemberbyUid(@PathVariable UUID uid){
        return memberRepository.findById(uid);
    }

    @GetMapping("/userForm")
    public ResponseEntity<MemberResponseForm> inquireMember(HttpServletRequest request) throws AuthenticationException {
        try {
            String authToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            UUID memberid = memberService.checkUserIdAndToken(authToken, refreshToken);
            Member searchMember = memberRepository.findById(memberid).get();
            MemberResponseForm successForm = MemberResponseForm.builder()
                    .message("유저 포스트 조회")
                    .state(HttpStatus.OK.value())
                    .data(MemberData.builder().Member(searchMember).build())
                    .build();
            return ResponseEntity.ok().body(successForm);
        }catch (RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping("/userForm/delete")
    public ResponseEntity<MemberResponseForm> deleteUser(HttpServletRequest request){
        try{
            String authToken= request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            UUID memberid = memberService.checkUserIdAndToken(authToken, refreshToken);
            Member deleteMember = memberRepository.findById(memberid).get();
            memberRepository.delete(deleteMember);
            MemberResponseForm successForm = MemberResponseForm.builder()
                    .state(HttpStatus.OK.value())
                    .message("회원 탈퇴 성공")
                    .data(MemberData.builder().Member(deleteMember).build())
                    .build();
            return ResponseEntity.ok().body(successForm);
        }catch (RuntimeException | AuthenticationException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping("/userForm/update")
    public ResponseEntity<MemberResponseForm> updateUser(@RequestPart(value = "metadata", required = true) Member member, @RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest request) throws AuthenticationException {
        try{
            String authToken= request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            String DefaultProfile = "https://firebasestorage.googleapis.com/v0/b/caps-1edf8.appspot.com/o/DefaultProfile.PNG?alt=media&token=266e52f4-818f-4a20-970d-2d84ba48e5a1";
            UUID memberid = memberService.checkUserIdAndToken(authToken, refreshToken);

            memberService.MemberUpdate(member, memberid, file);
            MemberResponseForm successForm = MemberResponseForm.builder()
                    .message("유저 업데이트 성공")
                    .state(HttpStatus.OK.value())
                    .data(MemberData.builder().Member(memberRepository.findById(memberid)).build()).build();
            return ResponseEntity.ok().body(successForm);
        }catch (RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseForm> allMemberByPage(@RequestParam int page){
        int wantCount = 12;
        Pageable pageable = PageRequest.of(page-1, wantCount);
        int maxPage = (int) Math.ceil((double) memberRepository.findAll().size() / wantCount);
        ResponseForm successResponse = ResponseForm.builder()
                .message(page + " 페이지 조회가 완료되었습니다.")
                .data(memberRepository.getAllMember(pageable))
                .state(maxPage)
                .build();
        return ResponseEntity.ok().body(successResponse);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseForm> doFilteringMember(@RequestParam("search") String search,@RequestParam("category") List<String> category,@RequestParam("subject") List<String> subject, @RequestParam("rule") List<String> rule,@RequestParam("page") int page){
        ResponseForm responseForm = memberService.doFilteringMember(category, subject, rule, search, page);
        return ResponseEntity.ok().body(responseForm);
    }

    @GetMapping("/check_email/{email}/exists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email){
        return ResponseEntity.ok(memberService.checkEmailDuplicate(email));
    }
    @GetMapping("/check_nickname/{nickname}/exists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname){
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

    @GetMapping("/test")
    public void testcode(){
        System.out.println("a");
        contestCrawlingService.crawlContest();
    }

    @GetMapping("/solved/{username}")
    public SolvedAcUser solvedtest(@PathVariable String username){
        return solvedacService.getUser(username);
    }

    @PostMapping("/send-email/{email}")
    public ResponseEntity<ResponseForm> sendEmail(@PathVariable String email) throws MessagingException {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        if(byEmail.isPresent()) {
            ResponseForm duplicateEmail=ResponseForm.builder().state(100).message("이미 존재하는 이메일입니다.").build();
            return ResponseEntity.ok(duplicateEmail);
        }
        try {
            memberService.sendVerificationEmail(email);
            ResponseForm responseForm=ResponseForm.builder().state(200).message("인증 코드를 보냈습니다").build();
            return ResponseEntity.ok(responseForm);
        }catch (MessagingException e){
            ResponseForm error=ResponseForm.builder().state(101).message(e.getMessage()).build();
            return ResponseEntity.ok(error);
        }

    }
    @PostMapping("/verify-email/{email}")
    public ResponseEntity<ResponseForm> verifyEmail(@PathVariable String email, @RequestParam("code")String code){
        try {
            ResponseForm responseForm = memberService.verifyEmail(email, code);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            return ResponseEntity.ok(ResponseForm.builder().state(100).message(e.getMessage()).build());
        }
    }

    @GetMapping("/recommend")
    public ResponseEntity<ResponseForm> recommendList(HttpServletRequest request,@RequestParam("category")String category,@RequestParam("field")String field,@RequestParam("sub")String sub){
        String accessToken=request.getHeader("login-token");
        String refreshToken=request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = memberService.recommendTeams(accessToken, refreshToken,category,field,sub);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/main")
    public ResponseEntity<ResponseForm> mainMember(){
        List<Member> randomMember = memberRepository.findRandomMembers();
        ResponseForm responseForm = ResponseForm.builder().message("랜덤 유저를 조회합니다").state(HttpStatus.OK.value()).data(randomMember).build();
        return ResponseEntity.ok().body(responseForm);
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseForm> Logout(HttpServletRequest request){
        String authToken= request.getHeader("login-token");
        String refreshToken = request.getHeader("refresh-token");
        ResponseForm responseForm=memberService.doLogout(authToken,refreshToken);
        return ResponseEntity.ok(responseForm);
    }
}
