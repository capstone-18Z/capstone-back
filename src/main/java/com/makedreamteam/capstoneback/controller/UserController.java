package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.User;
import com.makedreamteam.capstoneback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/users/signup")
    public User createUser(UserForm userForm){
        // 암호화 기능 추가 필요 (bCryptPasswordEncoder 사용)
        User user = new User();
        String rawPass = userForm.getPassword();
        String encPass = bCryptPasswordEncoder.encode(rawPass);
        Long userid = userForm.getUserid();
        String name = userForm.getName();
        user.setUserid(userid);
        user.setPassword(encPass);
        user.setName(name);
        userService.join(user);
        return user;
    }

    @GetMapping("/users")
    public List<User> alluser(){return userService.findMembers();}

    @GetMapping("/users/{userid}")
    public Optional<User> findUser(@PathVariable Long userid){
        return userService.findOne(userid);
    }
}
