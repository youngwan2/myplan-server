package com.myplan.server.util;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CookieUtil {

    public CookieUtil(){}

    public Cookie createCookie(String refresh, String value){
        Cookie cookie = new Cookie(refresh, value);
        cookie.setMaxAge(60*60*24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        log.info("{} = {}",cookie.getName(), cookie.getValue());
        return cookie;
    }

    public Cookie deleteCookie(String refresh, String value){
        Cookie cookie = new Cookie(refresh, value);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
