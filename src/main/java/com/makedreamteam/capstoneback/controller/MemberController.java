package com.makedreamteam.capstoneback.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.Role;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // 회원가입
    @PostMapping("/register")
    public UUID register(@RequestBody Map<String, String> user) {
        return memberRepository.save(Member.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .nickname(user.get("nickname"))
                .role(Role.ROLE_MEMBER)
                .build()).getId();
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        Member member = memberRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입 되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 맞지 않습니다.");
        }
        return jwtTokenProvider.createToken(member.getEmail(), member.getRole(), member.getNickname());
    }

    @GetMapping("/search/{email}")
    public Optional<Member> SearchMemberbyEmail(@PathVariable String email){
        return memberRepository.findByEmail(email);
    }

}
