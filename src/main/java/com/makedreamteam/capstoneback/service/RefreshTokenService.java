package com.makedreamteam.capstoneback.service;


import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Member;
import com.makedreamteam.capstoneback.domain.RefreshToken;
import com.makedreamteam.capstoneback.domain.Token;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import com.makedreamteam.capstoneback.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private long accesstokenValidTime =60*1000L;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
    }

    public Token createNewToKen(String refreshToken) {

            Claims userinfo= jwtTokenProvider.getClaimsToken(refreshToken);
            UUID userId=UUID.fromString((String)userinfo.get("userId"));
            Optional<Member> byId1 = memberRepository.findById(userId);
            if(byId1.isEmpty()){
                return null;
            }
            Member member=byId1.get();
            //새로운 리프레쉬토큰을 발급하고 db에 업데이트
            Optional<RefreshToken> byUserId = refreshTokenRepository.findByUserId(userId);
            RefreshToken refreshToken1=byUserId.get();

        //이미 refresh토큰의 유효성검사를 끝내고 받은 refresh요청이기때문에 추가로 refreshToken의 유효성을 검사할 필요는 없는듯 하다
            String newRefreshToken= jwtTokenProvider.createRefreshToken(userId);
            refreshToken1.setRefreshToken(newRefreshToken);
            refreshTokenRepository.save(refreshToken1);
            Date now = new Date();
            String newAccessToken= jwtTokenProvider.createAccessToken(userId,member.getEmail(),member.getRole(),member.getNickname());

            return Token.builder().refreshToken(newRefreshToken).accessToken(newAccessToken).exp(new Date(now.getTime() + accesstokenValidTime)).build();








    }
}
