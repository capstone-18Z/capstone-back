package com.makedreamteam.capstoneback.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.PostMember;
import com.makedreamteam.capstoneback.domain.Role;
import com.makedreamteam.capstoneback.domain.UserLang;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.repository.SpringDataJpaUserLangRepository;
import com.makedreamteam.capstoneback.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

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
    public String login(@RequestBody Map<String, String> user) {
        Member member = memberRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입 되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 맞지 않습니다.");
        }
        return jwtTokenProvider.createToken(member.getId(), member.getEmail(), member.getRole(), member.getNickname());
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
    public PostMember addPostMember(@RequestBody Map<String, Object> post){
        Optional<Member> member = memberRepository.findById(UUID.fromString((String) post.get("memberid")));
        PostMember postman = PostMember.builder()
                .userId(UUID.fromString((String) post.get("memberid")))
                .title((String) post.get("title"))
                .nickname(member.get().getNickname())
                .detail((String) post.get("detail"))
                .field((Integer) post.get("field"))
                .build();
        UserLang userLang = userLangRepository.save(UserLang.builder()
                .userid(UUID.fromString((String) post.get("memberid")))
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
}
