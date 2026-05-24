package com.smarthome.dto;

public class SessionInfo {
    private final Long userId;
    private final String username;

    public SessionInfo(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}
