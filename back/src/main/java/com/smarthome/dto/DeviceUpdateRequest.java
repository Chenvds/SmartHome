package com.smarthome.dto;

import java.util.Map;

public class DeviceUpdateRequest {
    private Map<String, Object> updates;

    public Map<String, Object> getUpdates() { return updates; }
    public void setUpdates(Map<String, Object> updates) { this.updates = updates; }
}
