package com.makedreamteam.capstoneback;

import java.util.*;

import com.makedreamteam.capstoneback.domain.RefreshToken;
import com.makedreamteam.capstoneback.domain.Role;
import com.makedreamteam.capstoneback.domain.Token;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "test";

    // 토큰 유효시간 30분
    private long accesstokenValidTime = 30*60*1000L;
    private long refreshtokenValidTime=30*60*10000L;

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey를 Base64로 인코딩
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public Token createToken(UUID id, String userPK, Role roles, String nickname) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id)); // JWT payload에 저장되는 정보 단위
        claims.put("email", userPK);
        claims.put("nickname", nickname);
        claims.put("roles", roles); // 정보 저장 (key-value)
        Date now = new Date();
        String accessToken=Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accesstokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature에 들어갈 secret 값 세팅
                .compact();
        String refreshToken=Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshtokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature에 들어갈 secret 값 세팅
                .compact();

        return Token.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPK(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPK(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN": "TOKEN 값"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {

            return false;
        }
    }
    public String recreationAccessToken(UUID id,String refreshToken){

        Claims claims = Jwts.claims().setSubject(String.valueOf(id)); // JWT payload 에 저장되는 정보단위
        // 정보는 key / value 쌍으로 저장된다.
        Jws<Claims> re = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
        System.out.println("re = "+refreshToken);
        Date now = new Date();
        claims.put("email", re.getBody().get("email"));
        claims.put("nickname", re.getBody().get("nickname"));
        claims.put("roles", re.getBody().get("roles"));
        //Access Token
        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accesstokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();



        return accessToken;
    }

    public String validateRefreshToken(String refreshTokenObj) {

        try {
            // 검증
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshTokenObj);
            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성합니다.
            if (!claims.getBody().getExpiration().before(new Date())) {

                return recreationAccessToken(UUID.fromString(claims.getBody().get("sub").toString()),refreshTokenObj);
            }
        }catch (ExpiredJwtException e) {
            //refresh 토큰이 만료되었을 경우, 로그인이 필요합니다.
            return null;
        }
        return null;
    }
}
