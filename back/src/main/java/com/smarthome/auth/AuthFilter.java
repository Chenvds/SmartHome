package com.smarthome.auth;

import com.smarthome.dto.SessionInfo;
import com.smarthome.entity.UserEntity;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AuthFilter implements Filter {

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (!path.startsWith("/api/")
            || path.startsWith("/api/v1/auth/")
            || path.startsWith("/api/v1/health")
            || "OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader("X-Auth-Token");
        UserEntity user = authService.authenticate(token);

        if (user == null) {
            res.setContentType("application/json;charset=UTF-8");
            res.setStatus(401);
            res.getWriter().write("{\"success\":false,\"message\":\"请先登录\",\"data\":null}");
            return;
        }

        req.setAttribute("currentUser", new SessionInfo(user.getId(), user.getUsername()));
        chain.doFilter(request, response);
    }
}
