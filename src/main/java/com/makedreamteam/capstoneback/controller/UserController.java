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


    @GetMapping("/users/signin")
    public String createForm() { return "users/createUserForm"; }

    @PostMapping("/users/signin")
    public String create(User user){
        userService.join(user);
        return "redirect:/";
    }

    @GetMapping("/users")
    public String list(Model model){
        List<User> members = userService.findMembers();
        model.addAttribute("users", members);
        System.out.println(members);
        return "users/userList";
    }
}
