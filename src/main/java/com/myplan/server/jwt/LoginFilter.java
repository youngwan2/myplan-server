package com.myplan.server.jwt;

import com.myplan.server.model.Refresh;
import com.myplan.server.repository.RefreshRepository;
import com.myplan.server.util.CookieUtil;
import com.myplan.server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final RefreshRepository refreshRepository;
    private final AuthenticationManager authenticationManager;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    static final long REFRESH_EXP=60 * 60 * 24 * 30 * 1000L;

    public LoginFilter(AuthenticationManager authenticationManager,RefreshRepository refreshRepository, CookieUtil cookieUtil, JwtUtil jwtUtil ) {
        this.authenticationManager = authenticationManager;
        this.refreshRepository = refreshRepository;
        this.cookieUtil = cookieUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("username:{},password:{}",username, password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password); // 로그인 요청할 자격증명 객체(토큰) 생성

        return authenticationManager.authenticate(authToken); // 토큰 인증 성공 시 인증된 사용자 자격증명이 담긴 Authentication 객체가 반환
    }

    // 성공시 JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal(); // 인증된 사용자 자격증명 반환

        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();

        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();

        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        // MEMO: 1000L is ms
        String accessToken = jwtUtil.createJwtToken(username, role, 60 * 60 * 10 * 1000L, "access"); // 10시간
        String refreshToken = jwtUtil.createJwtToken(username, role, REFRESH_EXP,"refresh"  ); // 30일

        // Refresh 토큰 저장
        addRefresh(username, refreshToken, REFRESH_EXP);

        response.setContentType("application/json");
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        // 응답 설정
        PrintWriter out =  response.getWriter();
        out.write("{\"message\": \"login successfully completed\"}");

        out.flush();
    }

    // 실패시 에러 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
        response.setStatus(401);
    }


    // refresh 토큰 데이터베이스 저장
    private void addRefresh(String username, String refresh, Long exp) {
        Refresh refreshModel = new Refresh();
        refreshModel.setUsername(username);
        refreshModel.setRefresh(refresh);
        refreshModel.setExp(System.currentTimeMillis() + exp);

        refreshRepository.save(refreshModel);
    }
}
