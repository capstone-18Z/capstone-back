package com.makedreamteam.capstoneback.controller;

import java.util.*;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.service.MemberService;
import com.makedreamteam.capstoneback.service.TeamService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
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

    @GetMapping("/search/uid/{uid}")
    public Optional<Member> SearchMemberbyUid(@PathVariable UUID uid){
        return memberRepository.findById(uid);
    }

    @PostMapping("/new")
    public ResponseEntity<MemberResponseForm> addPostMember(@RequestBody Map<String, Object> post, HttpServletRequest request) throws AuthenticationException {
        try {
            String authToken= request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            UUID memberid = memberService.checkUserIdAndToken(authToken, refreshToken);
            Optional<Member> member = memberRepository.findById(memberid);
            PostMember postman = PostMember.builder()
                    .userId(memberid)
                    .title((String) post.get("title"))
                    .nickname(member.get().getNickname())
                    .detail((String) post.get("detail"))
                    .field((Integer) post.get("field"))
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
                    .build();
            PostMember result = memberService.PostJoin(postman, authToken);
            MemberResponseForm successForm = MemberResponseForm.builder()
                    .message("유저 포스트 입력 성공")
                    .state(HttpStatus.OK.value())
                    .data(MemberData.builder().PostMember(result)
                            .Member(memberRepository.findById(memberid)).build()).build();
            return ResponseEntity.ok().body(successForm);
        } catch (AuthenticationException | RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
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
                    .data(MemberData.builder().Member(searchMember).build()).build();
            return ResponseEntity.ok().body(successForm);
        }catch (RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }

    }

    @PostMapping("/userForm/update")
    public ResponseEntity<MemberResponseForm> updateUser(@RequestBody Map<String, Object> updates, HttpServletRequest request) throws AuthenticationException {
        try{
            String authToken= request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            Claims claims = Jwts.parser()
                    .setSigningKey("test")
                    .parseClaimsJws(authToken)
                    .getBody();
            UUID memberid = memberService.checkUserIdAndToken(authToken, refreshToken);
            Member oldPost = memberRepository.findById(memberid)
                    .orElseThrow(() -> new RuntimeException("해당하는 게시물이 존재하지 않습니다."));
            updates.forEach((key, value) -> {
                switch (key) {
                    case "email":
                        oldPost.setEmail((String) value);
                        break;
                    case "nickname":
                        oldPost.setNickname((String) value);
                        break;
                    case "password":
                        oldPost.setPassword((String) value);
                        break;
                    case "c":
                        oldPost.setC((Integer) value);
                        break;
                    case "cpp":
                        oldPost.setCpp((Integer) value);
                        break;
                    case "cs":
                        oldPost.setCs((Integer) value);
                        break;
                    case "php":
                        oldPost.setPhp((Integer) value);
                        break;
                    case "vb":
                        oldPost.setVb((Integer) value);
                        break;
                    case "assembly":
                        oldPost.setAssembly((Integer) value);
                        break;
                    case "java":
                        oldPost.setJava((Integer) value);
                        break;
                    case "javascript":
                        oldPost.setJavascript((Integer) value);
                        break;
                    case "python":
                        oldPost.setPython((Integer) value);
                        break;
                    case "sqllang":
                        oldPost.setSqllang((Integer) value);
                        break;
                        // 필드가 추가될 때마다 case 추가
                }
            });
            memberRepository.save(oldPost);
            //Member updateMember = memberService.update(memberid, post);
            MemberResponseForm successForm = MemberResponseForm.builder()
                    .message("유저 업데이트 성공")
                    .state(HttpStatus.OK.value())
                    .data(MemberData.builder().Member(memberRepository.findById(memberid)).build()).build();
            return ResponseEntity.ok().body(successForm);
        }catch (RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping("/post/update")
    public ResponseEntity<MemberResponseForm> updatePost(@RequestBody Map<String, Object> updates, HttpServletRequest request) throws AuthenticationException{
        try{
            String loginToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            Long postid = (Long) updates.get("postid");
            UUID userid = memberService.checkUserIdAndToken(loginToken, refreshToken);
            UUID checkid = postMemberRepository.findByPostId(postid).get().getUserId();
            System.out.println(checkid.toString());
            System.out.println(userid.toString());
            if(checkid.toString().equals(userid.toString())){
                // 해당 게시물이 본인의 게시물이 맞다면 삭제
                PostMember oldPost = postMemberRepository.findById(postid)
                        .orElseThrow(() -> new RuntimeException("해당하는 게시물이 존재하지 않습니다."));
                updates.forEach((key, value) -> {
                    switch (key) {
                        case "title":
                            oldPost.setTitle((String) value);
                            break;
                        case "field":
                            oldPost.setField((Integer) value);
                            break;
                        case "detail":
                            oldPost.setDetail((String) value);
                            break;
                        case "c":
                            oldPost.setC((Integer) value);
                            break;
                        case "cpp":
                            oldPost.setCpp((Integer) value);
                            break;
                        case "cs":
                            oldPost.setCs((Integer) value);
                            break;
                        case "php":
                            oldPost.setPhp((Integer) value);
                            break;
                        case "vb":
                            oldPost.setVb((Integer) value);
                            break;
                        case "assembly":
                            oldPost.setAssembly((Integer) value);
                            break;
                        case "java":
                            oldPost.setJava((Integer) value);
                            break;
                        case "javascript":
                            oldPost.setJavascript((Integer) value);
                            break;
                        case "python":
                            oldPost.setPython((Integer) value);
                            break;
                        case "sqllang":
                            oldPost.setSqllang((Integer) value);
                            break;
                        case "keyword":
                            oldPost.setMemberKeywords((List<MemberKeyword>) value);
                        // 필드가 추가될 때마다 case 추가
                    }
                });

                postMemberRepository.save(oldPost);
                MemberResponseForm successForm = MemberResponseForm.builder()
                        .message("유저 포스트 수정 성공")
                        .state(HttpStatus.OK.value())
                        .data(MemberData.builder().PostMember(oldPost).build())
                        .build();
                return ResponseEntity.ok().body(successForm);
            }
            else{
                MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                        .message("본인의 게시물이 아닙니다.").state(HttpStatus.BAD_REQUEST.value()).build();
                return ResponseEntity.badRequest().body(errorResponseForm);
            }

        }catch (RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        }
    }

    @PostMapping("/post/delete")
    public ResponseEntity<MemberResponseForm> deletePost(@RequestParam("postid") Long postid, HttpServletRequest request) throws AuthenticationException {
        try{
            String loginToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            UUID userid = memberService.checkUserIdAndToken(loginToken, refreshToken);
            UUID checkid = postMemberRepository.findByPostId(postid).get().getUserId();
            System.out.println(checkid.toString());
            System.out.println(userid.toString());
            if(checkid.toString().equals(userid.toString())){
                // 해당 게시물이 본인의 게시물이 맞다면 삭제
                memberService.delete(postid, loginToken, refreshToken);
                MemberResponseForm successForm = MemberResponseForm.builder()
                        .message("유저 포스트 삭제 성공")
                        .state(HttpStatus.OK.value())
                        .build();
                return ResponseEntity.ok().body(successForm);
            }
            else{
                MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                        .message("본인의 게시물이 아닙니다.").state(HttpStatus.BAD_REQUEST.value()).build();
                return ResponseEntity.badRequest().body(errorResponseForm);
            }
        }catch (RuntimeException e){
            MemberResponseForm errorResponseForm = MemberResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        } catch (RefreshTokenExpiredException e) {
            throw new RuntimeException(e);
        } catch (TokenException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
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

    @GetMapping("/recommand")
    public List<Team> TeamRecommand(HttpServletRequest request) throws AuthenticationException {
        String authToken= request.getHeader("login-token");
        String refreshToken = request.getHeader("refresh-token");
        UUID uid = memberService.checkUserIdAndToken(authToken, refreshToken);
        return memberService.recommendTeams(uid, 2);
    }

    @PostMapping("/post/new")
    public ResponseEntity<ResponseForm> addNewPost(@RequestBody PostMember postMember,HttpServletRequest request){
        try {
            String loginToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            ResponseForm responseForm = memberService.testAddNewMember(postMember, loginToken, refreshToken);
            return ResponseEntity.ok().body(responseForm);
        } catch (RefreshTokenExpiredException e) {
            ResponseForm responseForm=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseForm);
        } catch (TokenException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
