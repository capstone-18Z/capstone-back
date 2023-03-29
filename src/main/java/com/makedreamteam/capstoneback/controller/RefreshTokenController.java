package com.makedreamteam.capstoneback.controller;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Token;
import com.makedreamteam.capstoneback.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@CrossOrigin
public class RefreshTokenController {


    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/{userId}/refresh")
    public ResponseEntity<Token> makeNewToken(@PathVariable UUID userId){
        Token newToken=refreshTokenService.createNewToKen(userId);
        return ResponseEntity.ok().body(newToken);
    }


}
