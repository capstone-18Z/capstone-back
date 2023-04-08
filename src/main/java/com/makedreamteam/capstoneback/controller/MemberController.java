package com.makedreamteam.capstoneback.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.*;
import com.makedreamteam.capstoneback.exception.*;
import com.makedreamteam.capstoneback.form.PostResponseForm;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.PostMemberRepository;
import com.makedreamteam.capstoneback.service.FileService;
import com.makedreamteam.capstoneback.service.MemberService;
import com.makedreamteam.capstoneback.service.TeamService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;
    private final FileService fileService;
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
            Member oldMember = memberRepository.findById(memberid)
                    .orElseThrow(() -> new RuntimeException("해당하는 회원이 존재하지 않습니다."));

            for (Field field : oldMember.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                if (updates.containsKey(fieldName)) {
                    field.setAccessible(true);
                    Object value = updates.get(fieldName);
                    field.set(oldMember, value);
                }
            }
            memberRepository.save(oldMember);
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
        } catch (IllegalAccessException e) {
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

    @GetMapping("/post")
    public List<PostMember> findAllPostMember(){
        return postMemberRepository.findAll();
    }

    @PostMapping("/post/new")
    public ResponseEntity<PostResponseForm> addNewPost(@RequestPart(value = "metadata", required = true) PostMember postMember, @RequestPart(value = "files", required = false) MultipartFile[] files, HttpServletRequest request){
        try {
            String loginToken = request.getHeader("login-token");
            String refreshToken=request.getHeader("refresh-token");
            UUID userid = memberService.checkUserIdAndToken(loginToken, refreshToken);
            PostResponseForm responseForm = memberService.testAddNewMember(postMember, loginToken, refreshToken);
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        continue;
                    }
                    fileService.uploadFile(file, userid, responseForm.getPostid());
                }
            }
            return ResponseEntity.ok().body(responseForm);
        } catch (RefreshTokenExpiredException e) {
            PostResponseForm responseForm = PostResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseForm);
        } catch (TokenException | AuthenticationException | DatabaseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseForm.builder().message("Error occurred while uploading file.").build());
        }
    }

    @PostMapping("/post/delete/{postid}")
    public ResponseEntity<PostResponseForm> deletePost(@PathVariable Long postid, HttpServletRequest request) throws AuthenticationException {
        try{
            String loginToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            UUID userid = memberService.checkUserIdAndToken(loginToken, refreshToken);
            UUID checkid = postMemberRepository.findByPostId(postid).get().getMember().getId();
            System.out.println(checkid.toString());
            System.out.println(userid.toString());
            if(checkid.toString().equals(userid.toString())){
                // 해당 게시물이 본인의 게시물이 맞다면 삭제
                memberService.deletePost(postid, loginToken, refreshToken);
                PostResponseForm successForm = PostResponseForm.builder()
                        .message("유저 포스트 삭제 성공")
                        .state(HttpStatus.OK.value())
                        .build();
                return ResponseEntity.ok().body(successForm);
            }
            else{
                PostResponseForm errorResponseForm = PostResponseForm.builder()
                        .message("본인의 게시물이 아닙니다.").state(HttpStatus.BAD_REQUEST.value()).build();
                return ResponseEntity.badRequest().body(errorResponseForm);
            }
        }catch (RuntimeException e){
            PostResponseForm errorResponseForm = PostResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        } catch (RefreshTokenExpiredException | TokenException | DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/post/update/{postid}")
    public ResponseEntity<PostResponseForm> updatePost(@RequestPart(value = "metadata", required = true) Map<String, Object> updates, @RequestPart(value = "files", required = false) MultipartFile[] files, @PathVariable Long postid, HttpServletRequest request) throws AuthenticationException{
        try{
            String loginToken = request.getHeader("login-token");
            String refreshToken = request.getHeader("refresh-token");
            UUID userid = memberService.checkUserIdAndToken(loginToken, refreshToken);
            UUID checkid = postMemberRepository.findByPostId(postid).get().getMember().getId();
            System.out.println(checkid.toString());
            System.out.println(userid.toString());
            if(checkid.toString().equals(userid.toString())){
                // 해당 게시물이 본인의 게시물이 맞다면 삭제
                PostMember oldPost = postMemberRepository.findById(postid)
                        .orElseThrow(() -> new RuntimeException("해당하는 게시물이 존재하지 않습니다."));
                for (Field field : oldPost.getClass().getDeclaredFields()) {
                    String fieldName = field.getName();
                    if (updates.containsKey(fieldName)) {
                        field.setAccessible(true);
                        Object value = updates.get(fieldName);
                        field.set(oldPost, value);
                    }
                }
                PostMember postMember = postMemberRepository.findByPostId(postid).get();
                System.out.println("변경 전 FileData Size : "+postMemberRepository.findById(postid).get().getFileDataList().size());

                if (files != null) {
                    List<FileData> newList = new ArrayList<>();
                    List<FileData> fileList = postMember.getFileDataList();
                    for (FileData file : fileList) {
                        System.out.println("파일 이름 : "+file.getFileName());
                        fileService.deleteFile(file);
                    }
                    oldPost.getFileDataList().clear();
                    for (MultipartFile file : files) {
                        if (file.isEmpty()) {
                            continue;
                        }
                        newList.add(fileService.uploadFile(file, userid, postid));
                    }
                    oldPost.setFileDataList(newList);
                }

                memberService.updateMemberPost(oldPost, loginToken, refreshToken);
                System.out.println("변경 후 FileData Size : "+postMemberRepository.findById(postid).get().getFileDataList().size());
                PostResponseForm successForm = PostResponseForm.builder()
                        .message("유저 포스트 수정 성공")
                        .state(HttpStatus.OK.value())
                        .data(MemberData.builder().PostMember(oldPost).build())
                        .postid(postid)
                        .build();
                return ResponseEntity.ok().body(successForm);
            }
            else{
                PostResponseForm errorResponseForm = PostResponseForm.builder()
                        .message("본인의 게시물이 아닙니다.").state(HttpStatus.BAD_REQUEST.value()).build();
                return ResponseEntity.badRequest().body(errorResponseForm);
            }

        }catch (RuntimeException e){
            PostResponseForm errorResponseForm = PostResponseForm.builder()
                    .message(e.getMessage()).state(HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.badRequest().body(errorResponseForm);
        } catch (RefreshTokenExpiredException | LoginTokenExpiredException | TokenException | CannotFindTeamOrMember |
                 DatabaseException | IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/post/{postid}")
    public ResponseEntity<PostResponseForm> getPost(@PathVariable Long postid){
        try {
            PostMember postMember = postMemberRepository.findByPostId(postid).get();
            List<String> filenames = postMember.getFileDataList().stream()
                    .map(FileData::getFileName)
                    .collect(Collectors.toList());
            PostResponseForm responseForm = PostResponseForm.builder()
                    .message("포스트 조회가 완료되었습니다.")
                    .data(postMember)
                    .postid(postid)
                    .filenames(filenames)
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
