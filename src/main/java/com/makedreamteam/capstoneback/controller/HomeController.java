package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HomeController {
    private final ContestRepository contestRepository;
    @GetMapping("/")
    public String home(){
        return "home";
    }
    @GetMapping("/users/register")
    public String createForm() { return "users/createUserForm"; }

    @GetMapping("/users/login")
    public String login(){
        return "users/signinForm";
    }

    @GetMapping("/user/logincheck")
    public String logincheck(){
        return "securitycheck";
    }

    @GetMapping("/contest")
    public ResponseEntity<ResponseForm> getAllContest(){
        try{
            ResponseForm responseForm = ResponseForm.builder()
                    .message("공모전 전체조회 완료")
                    .data(contestRepository.findAll())
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

