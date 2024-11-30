package com.myplan.server.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // 생성자 호출과 동시에 비밀키 암호화 처리 및 초기화
    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 에서 username 추출
    public String getUsername(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // JWT 에서 role 추출
    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Date getExp(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    // JWT 만료 여부 확인(토큰 만료시간이 현재 시간 이전 이라면 토큰은 만료된 것)
    public Boolean isExp(String token){
        log.info("Exp of created token :{}, current date: {} ",getExp(token).toString(), new Date(System.currentTimeMillis()));
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date(System.currentTimeMillis()));
    }

    // JWT 토큰 유형
    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // jwt 토큰 생성 후 반환
    public String createJwtToken(String username, String role, Long expiredMs, String access){
        log.info("createdAt: {}, expiration:{}",new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + expiredMs) );
        return Jwts.builder()
                .claim("username", username) // 유저 식별할 이름
                .claim("role",role) // 권한
                .claim("category", access)
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 생성 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 토큰 만료 시간
                .signWith(secretKey) // 비밀키
                .compact(); // jwt 생성

    }
}
