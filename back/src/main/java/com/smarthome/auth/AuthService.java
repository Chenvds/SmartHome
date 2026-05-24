package com.smarthome.auth;

import com.smarthome.entity.UserEntity;
import com.smarthome.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByUsername("admin")) {
            UserEntity u = new UserEntity();
            u.setUsername("admin");
            u.setPasswordHash(hashPassword("admin123"));
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("theme", "default");
            u.setPreferences(prefs);
            userRepository.save(u);
        }
    }

    @Transactional
    public UserEntity register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(hashPassword(password));
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("theme", "default");
        user.setPreferences(prefs);
        user.setToken(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity login(String username, String password) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (!verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误");
        }
        user.setToken(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public UserEntity authenticate(String token) {
        if (token == null || token.isEmpty()) return null;
        return userRepository.findByToken(token).orElse(null);
    }

    @Transactional
    public void logout(String token) {
        userRepository.findByToken(token).ifPresent(user -> {
            user.setToken(null);
            userRepository.save(user);
        });
    }

    private String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Hash error", e);
        }
    }

    private boolean verifyPassword(String password, String stored) {
        try {
            byte[] combined = Base64.getDecoder().decode(stored);
            byte[] salt = new byte[16];
            byte[] hash = new byte[combined.length - 16];
            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, hash, 0, hash.length);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] computed = md.digest(password.getBytes());
            return MessageDigest.isEqual(hash, computed);
        } catch (Exception e) {
            return false;
        }
    }
}
