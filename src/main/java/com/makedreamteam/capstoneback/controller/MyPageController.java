package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Course;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.HansungCrawlingService;
import com.makedreamteam.capstoneback.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final HansungCrawlingService hansungCrawlingService;

    public MyPageController(MyPageService myPageService, HansungCrawlingService hansungCrawlingService) {
        this.myPageService = myPageService;
        this.hansungCrawlingService = hansungCrawlingService;
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
    @PostMapping("/hansung")
    public ResponseEntity<ResponseForm> getHansungInfo(@RequestParam String id,@RequestParam String password){
        List<Course> courseList = hansungCrawlingService.crawlCourse(id, password);
        return ResponseEntity.ok().body(ResponseForm.builder().data(courseList).build());
    }

}
