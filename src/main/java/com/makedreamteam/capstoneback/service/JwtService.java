package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.RefreshToken;
import com.makedreamteam.capstoneback.domain.Token;
import com.makedreamteam.capstoneback.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {


    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void login(Token tokenDto){

        RefreshToken refreshToken = RefreshToken.builder().userId(String.valueOf(tokenDto.getKey())).refreshToken(tokenDto.getRefreshToken()).build();
        String loginUserId = refreshToken.getUserId();
        if(refreshTokenRepository.existsByUserId(loginUserId)){
            //log.info("기존의 존재하는 refresh 토큰 삭제");
            refreshTokenRepository.deleteByUserId(loginUserId);
        }
        refreshTokenRepository.save(refreshToken);

    }

    public Optional<RefreshToken> getRefreshToken(String refreshToken){

        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

//    public Map<String, String> validateRefreshToken(String refreshToken){
//        RefreshToken refreshToken1 = getRefreshToken(refreshToken).get();
//        String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken1);
//
//        return createRefreshJson(createdAccessToken);
//    }

    public Map<String, String> createRefreshJson(String createdAccessToken){

        Map<String, String> map = new HashMap<>();
        if(createdAccessToken == null){

            map.put("errortype", "Forbidden");
            map.put("status", "402");
            map.put("message", "Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");


            return map;
        }
        //기존에 존재하는 accessToken 제거


        map.put("status", "200");
        map.put("message", "Refresh 토큰을 통한 Access Token 생성이 완료되었습니다.");
        map.put("accessToken", createdAccessToken);

        return map;


    }

}
