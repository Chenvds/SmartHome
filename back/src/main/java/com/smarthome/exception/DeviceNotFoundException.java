package com.smarthome.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String deviceId) {
        super("设备未找到: " + deviceId);
    }
}
