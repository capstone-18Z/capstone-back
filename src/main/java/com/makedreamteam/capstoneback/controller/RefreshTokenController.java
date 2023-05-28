package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.domain.Token;
import com.makedreamteam.capstoneback.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin
public class RefreshTokenController {


    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Token> makeNewToken(@RequestBody Map<String,Object> data){
        String refreshToken=(String)data.get("refreshToken");
        System.out.println(refreshToken);
        Token newToken=refreshTokenService.createNewToKen(refreshToken);
        return ResponseEntity.ok().body(newToken);
    }


}
