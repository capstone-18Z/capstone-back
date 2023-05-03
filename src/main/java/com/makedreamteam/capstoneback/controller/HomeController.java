package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Contest;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<ResponseForm> getAllContest(@RequestParam int page){
        try{
            int wantPage = 12;
            Pageable pageable = PageRequest.of(page-1, wantPage);
            int maxPage = (int) Math.ceil((double) contestRepository.findAll().size() / wantPage);
            List<Contest> allContest = contestRepository.getAllContest(pageable);
            ResponseForm responseForm = ResponseForm.builder()
                    .state(maxPage)
                    .message("공모전 전체조회 완료")
                    .data(allContest)
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/contest/{title}")
    public ResponseEntity<ResponseForm> getSearchContest(@PathVariable String title, @RequestParam int page){
        try{
            int wantPage = 12;
            Pageable pageable = PageRequest.of(page-1, wantPage);
            int maxPage = (int) Math.ceil((double) contestRepository.findAll().size() / wantPage);
            List<Contest> allContest = contestRepository.findByTitleContainsOrderByCidDesc(title, pageable);
            ResponseForm responseForm = ResponseForm.builder()
                    .state(maxPage)
                    .message(page + "페이지 공모전 조회 완료")
                    .data(allContest)
                    .build();
            return ResponseEntity.ok().body(responseForm);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

