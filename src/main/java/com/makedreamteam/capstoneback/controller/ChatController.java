package com.makedreamteam.capstoneback.controller;


import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    ChatService chatService;


    @GetMapping("")
    public ResponseEntity<ResponseForm> getAllChat(HttpServletRequest request, @RequestParam("roomId") UUID roomId){
        String accessToken= request.getHeader("login-token");
        String refreshToken=request.getHeader("refresh-token");
        try {
            ResponseForm responseForm = chatService.getAllChat(roomId, accessToken, refreshToken);
            return ResponseEntity.ok(responseForm);
        }catch (RuntimeException e){
            ResponseForm error=ResponseForm.builder().message(e.getMessage()).build();
            return ResponseEntity.ok(error);
        }
    }
}
