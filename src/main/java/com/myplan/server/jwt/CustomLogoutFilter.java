package com.myplan.server.jwt;

import com.myplan.server.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;


    public CustomLogoutFilter(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    // 최상위 서블릿 요청, 응답을 http 전용 서블릿 요청 및 응답으로 다운 캐스팅
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
//        if (requestUri.matches("/api/users/register")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if (requestUri.matches("/api/users/") && request.getMethod().equals("PATCH")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        // 로그아웃 경로 혹은 POST 요청이 아니면 다음 필터로 넘김
        if (!requestUri.matches("^/logout$") || !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 refresh 토큰 읽어온 후 refresh 변수에 할당
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // 유효한 refresh 토큰이 아니라면(토큰이 없거나, 만료되었거나, refresh 토큰이 아니거나)
        if (refresh == null || jwtUtil.isExp(refresh) || !jwtUtil.getCategory(refresh).equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        // DB에 refresh 토큰이 저장되어 있지 않다면
        if (!refreshRepository.existsByRefresh(refresh)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        // refresh 토큰 DB 제거
        refreshRepository.deleteByRefresh(refresh);

        // 쿠키에 저장된 refresh 토큰 초기화(강제 만료 시키기)
        Cookie cookie = removeRefreshCookie();

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }


    private Cookie removeRefreshCookie() {
        Cookie cookie = new Cookie("refresh", null); // 쿠키 초기화
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
