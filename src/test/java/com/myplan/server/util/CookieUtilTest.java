package com.myplan.server.util;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CookieUtilTest {

    @Test
    void testCreateCookie() {
        // Given
        CookieUtil cookieUtil =new CookieUtil();

        String cookieName = "refreshToken";
        String cookieValue = "testValue";

        // When
        Cookie cookie = cookieUtil.createCookie(cookieName, cookieValue);

        // Then
        assertNotNull(cookie,"Cookie should not be null");
        assertEquals(cookieName, cookie.getName(),"Cookie name should match");
        assertEquals(cookieValue, cookie.getValue(), "Cookie value should match");
        assertEquals(60 * 60 *24, cookie.getMaxAge(), "Cookie max age should be 24 hours");
        assertEquals("/", cookie.getPath(),"Cookie path should be root");
        assertTrue(cookie.isHttpOnly(), "Cookie should be HttpOnly");

    }
}
