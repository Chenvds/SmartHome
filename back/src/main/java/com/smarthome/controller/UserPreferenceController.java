package com.smarthome.controller;

import com.smarthome.auth.AuthService;
import com.smarthome.dto.ApiResponse;
import com.smarthome.dto.SessionInfo;
import com.smarthome.entity.UserEntity;
import com.smarthome.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserPreferenceController {

    private final UserRepository userRepository;

    public UserPreferenceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/preferences")
    public ApiResponse<Map<String, Object>> getPreferences(HttpServletRequest req) {
        SessionInfo session = (SessionInfo) req.getAttribute("currentUser");
        UserEntity user = userRepository.findById(session.getUserId()).orElseThrow();
        return ApiResponse.ok(user.getPreferences());
    }

    @PutMapping("/preferences")
    @Transactional
    public ApiResponse<Map<String, Object>> updatePreferences(
            HttpServletRequest req,
            @RequestBody Map<String, Object> body) {
        SessionInfo session = (SessionInfo) req.getAttribute("currentUser");
        UserEntity user = userRepository.findById(session.getUserId()).orElseThrow();
        Map<String, Object> prefs = user.getPreferences();
        prefs.putAll(body);
        user.setPreferences(prefs);
        userRepository.save(user);
        return ApiResponse.ok("偏好已更新", prefs);
    }
}
