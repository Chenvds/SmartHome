package com.smarthome.controller;

import com.smarthome.auth.AuthService;
import com.smarthome.dto.ApiResponse;
import com.smarthome.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null || username.isBlank() || password.length() < 3) {
            return ApiResponse.fail("用户名不能为空，密码至少3位");
        }
        try {
            UserEntity user = authService.register(username, password);
            return ApiResponse.ok("注册成功", Map.of(
                "username", user.getUsername(),
                "token", user.getToken()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ApiResponse.fail("用户名和密码不能为空");
        }
        try {
            UserEntity user = authService.login(username, password);
            return ApiResponse.ok("登录成功", Map.of(
                "username", user.getUsername(),
                "token", user.getToken()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest req) {
        String token = req.getHeader("X-Auth-Token");
        authService.logout(token);
        return ApiResponse.ok("已退出", null);
    }
}
