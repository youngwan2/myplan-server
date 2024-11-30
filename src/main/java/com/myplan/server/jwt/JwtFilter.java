package com.myplan.server.jwt;

import com.myplan.server.exception.InvalidTokenException;
import com.myplan.server.model.Member;
import com.myplan.server.service.RefreshService;
import com.myplan.server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 이 필터는 클라이언트 요청마다 매번 실행됩니다.
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final RefreshService refreshService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = getAuthorizationHeader(request);

        if (request.getRequestURI().matches("/api/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 유효성 체크
        if (!isTokenValid(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료시간 체크
        String token = extractToken(authorization);
        if (jwtUtil.isExp(token)) {
            log.info("access 토큰이 만료되었기에 refresh 토큰을 사용하여 새 토큰을 생성합니다.");
            String newAccessToken = handleExpiredAccessToken(request, response);
            if (newAccessToken == null) return;
        }

        // 토큰으로 자격증명 객체 생성 후 저장
        Authentication authentication = createAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Auth O");
        filterChain.doFilter(request, response);
    }

    // refresh 토큰을 통한 access + refresh 토큰 재발급
    private String handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            return refreshService.refresh(request, response);
        } catch (InvalidTokenException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }
        return null;
    }


    // Authorization 헤더 가져오기
    private String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰이 유효한지 검증
    private boolean isTokenValid(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("Token is null or does not start with Bearer");
            return false;
        }
        return true;
    }

    // Authorization 헤더에서 순수 토큰 추출
    private String extractToken(String authorization) {
        return authorization.split(" ")[1];
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String token) {
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        Member user = Member.builder()
                .username(username)
                .password("temppassword") // 실제 비밀번호는 보안상의 이유로 저장하지 않음
                .role(role).build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }
}