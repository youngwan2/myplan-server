package com.myplan.server.service;

import com.myplan.server.exception.InvalidTokenException;
import com.myplan.server.exception.NotFoundException;
import com.myplan.server.util.JwtUtil;
import com.myplan.server.model.Refresh;
import com.myplan.server.repository.RefreshRepository;
import com.myplan.server.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshService {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshRepository refreshRepository;

    public void removeRefresh(String username) {
        Refresh refresh = refreshRepository.findOneByUsername(username);

        if(refresh == null) throw new NotFoundException("Refresh is not found");

        cookieUtil.deleteCookie("refresh", null); // 쿠키 삭제
        refreshRepository.deleteAllByUsername(username); // DB 삭제
    }

    public String refresh(HttpServletRequest request, HttpServletResponse response) {
        // refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        boolean hasRefresh = refreshRepository.existsByRefresh(refresh);

        if (!hasRefresh) {
            throw new InvalidTokenException("The refresh token does not exist in the database");
        }

        // refresh 토큰이 null 인 경우
        if (refresh == null) {
            throw new InvalidTokenException("refresh token null");
        }

        // refresh 토큰이 만료된 경우
        boolean isExp = jwtUtil.isExp(refresh);
        if (isExp) {
            throw new InvalidTokenException("refresh token expired");
        }

        // 현재 검증 토큰이 refresh 토큰이 아니라면
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new InvalidTokenException("invalid refresh token");
        }

        //  refresh 토큰을 사용해서 access 토큰 재발급
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createJwtToken(username, role, 60 * 60 * 10 * 1000L, "access"); // 10시간
        String newRefresh = jwtUtil.createJwtToken(username, role, 60 * 60 * 24 * 30 * 1000L, "refresh"); // 30일


        // 응답
        response.setHeader("access", newAccess);
        response.addCookie(cookieUtil.createCookie("refresh", newRefresh));

        return newAccess;
    }
}
