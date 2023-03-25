package com.makedreamteam.capstoneback.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import com.makedreamteam.capstoneback.service.MemberService;
import com.makedreamteam.capstoneback.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;
    private final SpringDataJpaUserLangRepository userLangRepository;
    private final TeamService teamService;

    // 회원가입
    @PostMapping("/register")
    public Member register(@RequestBody Map<String, String> user) {
        Member member = Member.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .nickname(user.get("nickname"))
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
            MemberResponseForm memberResponseForm = MemberResponseForm.builder()
                    .data(MemberData.builder().Token(jwtTokenProvider.createToken(member.getId(), member.getEmail(), member.getRole(),
                            member.getNickname())).build())
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

    @GetMapping("/search/uid/{uid}")
    public Optional<Member> SearchMemberbyUid(@PathVariable UUID uid){
        return memberRepository.findById(uid);
    }

    @PostMapping("/new")
    public PostMember addPostMember(@RequestBody Map<String, Object> post, HttpServletRequest request) throws AuthenticationException {
        String authToken= request.getHeader("login-token");
        if(authToken==null)
            throw new RuntimeException("로그인 상태가 아닙니다.");
        UUID memberid = memberService.checkUserIdAndToken(authToken);
        Optional<Member> member = memberRepository.findById(memberid);
        PostMember postman = PostMember.builder()
                .userId(memberid)
                .title((String) post.get("title"))
                .nickname(member.get().getNickname())
                .detail((String) post.get("detail"))
                .field((Integer) post.get("field"))
                .build();
        UserLang userLang = userLangRepository.save(UserLang.builder()
                .userid(memberid)
                .c((Integer) post.get("c"))
                .cs((Integer) post.get("cs"))
                .php((Integer) post.get("php"))
                .cpp((Integer) post.get("cpp"))
                .vb((Integer) post.get("vb"))
                .assembly((Integer) post.get("assembly"))
                .java((Integer) post.get("java"))
                .javascript((Integer) post.get("javascript"))
                .python((Integer) post.get("python"))
                .sqllang((Integer) post.get("sqllang"))
                .build());
        return memberService.PostJoin(postman);
    }

    @GetMapping("/all")
    public List<Member> allMember(){
        return memberRepository.findAll();
    }

    @GetMapping("/check_email/{email}/exists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email){
        return ResponseEntity.ok(memberService.checkEmailDuplicate(email));
    }
    @GetMapping("/check_nickname/{nickname}/exists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname){
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

    @GetMapping("/recommand/{uid}")
    public List<Team> TeamRecommand(@PathVariable UUID uid){
        return teamService.recommandTeams(uid, 2);
    }
}
