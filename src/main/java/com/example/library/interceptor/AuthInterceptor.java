package com.example.library.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {

    private static final String LOGIN_URL = "/users/login";
    private static final String USER_KEY = "loggedInUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        if (requestURI.equals(LOGIN_URL) || requestURI.equals("/users/login/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute(USER_KEY);
            if (user != null) {
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"请先登录\",\"code\":401}");
        return false;
    }
}
