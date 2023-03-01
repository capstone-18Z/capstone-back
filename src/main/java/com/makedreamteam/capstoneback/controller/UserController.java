package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.User;
import com.makedreamteam.capstoneback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){ this.userService = userService; }


    @GetMapping("/users/signup")
    public String createForm() { return "users/createUserForm"; }

    @PostMapping("/users/signup")
    public String create(User user){
        // 암호화 기능 추가 필요 (bCryptPasswordEncoder 사용)
        userService.join(user);
        return "redirect:/";
    }

    @GetMapping("/users/signin")
    public String signinForm(){
        return "users/signinForm";
    }

    @GetMapping("/users")
    public String list(Model model){
        List<User> members = userService.findMembers();
        model.addAttribute("users", members);
        System.out.println(members);
        return "users/userList";
    }
}
