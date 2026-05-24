package com.smarthome.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users")
public class UserEntity {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 128)
    private String passwordHash;

    @Column(length = 64)
    private String token;

    @Column(columnDefinition = "TEXT")
    private String preferencesJson;

    public UserEntity() {}

    public Map<String, Object> getPreferences() {
        if (preferencesJson == null || preferencesJson.isBlank()) return new HashMap<>();
        try {
            return MAPPER.readValue(preferencesJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setPreferences(Map<String, Object> prefs) {
        try {
            this.preferencesJson = MAPPER.writeValueAsString(prefs);
        } catch (Exception e) {
            this.preferencesJson = "{}";
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPreferencesJson() { return preferencesJson; }
    public void setPreferencesJson(String preferencesJson) { this.preferencesJson = preferencesJson; }
}
