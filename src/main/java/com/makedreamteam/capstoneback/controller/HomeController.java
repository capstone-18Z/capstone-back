package com.makedreamteam.capstoneback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(){
        System.out.println("hello world!");
        return "home";
    }
    @GetMapping("/users/signup")
    public String createForm() { return "users/createUserForm"; }

    @GetMapping("/users/signin")
    public String login(){
        return "users/signinForm";
    }
}

