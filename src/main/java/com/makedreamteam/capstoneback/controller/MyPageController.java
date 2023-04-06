package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }
    @GetMapping("")
    public ResponseEntity<ResponseForm> showMyPage(HttpServletRequest request){
        try{
            String refreshToken= request.getHeader("refresh-token");
            String accessToken= request.getHeader("login-token");

            ResponseForm myPageInfo = myPageService.getMyPageInfo(accessToken, refreshToken);
            return ResponseEntity.ok().body(myPageInfo);
        }catch (RuntimeException e){
            ResponseForm error= ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(error);
        }

    }

}
