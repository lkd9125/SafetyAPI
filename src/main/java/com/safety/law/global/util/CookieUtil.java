package com.safety.law.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private final Integer DEFULAT_COOKIE_TIME = 60 * 60 * 24;

    public static Cookie getCookie(HttpServletRequest request, String cookieName){

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("visit_cookie")) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static void createCookie(HttpServletResponse response, Cookie cookie){
        response.addCookie(cookie);
    }


}