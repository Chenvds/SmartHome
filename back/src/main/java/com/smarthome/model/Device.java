package com.smarthome.model;

import java.util.Map;

public class Device {
    private String id;
    private String name;
    private String icon;
    private String type;
    private boolean state;
    private String room;
    private String iconBg;
    private Integer power;
    private Integer brightness;
    private String color;
    private Integer temp;
    private String mode;
    private Integer openPercent;
    private Integer volume;
    private String input;
    private Integer speed;
    private Boolean locked;
    private Integer tempSet;
    private Boolean recording;
    private String playing;
    private Integer humidity;
    private Integer waterLevel;
    private Integer battery;
    private Boolean alarm;
    private Integer tempFridge;
    private Integer tempFreezer;
    private String resolution;
    private String storage;
    private String lastUnlock;
    private String lastEvent;
    private Boolean closed;

    public Device() {}

    // 从 JSON map 中创建 Device（支持部分字段更新）
    public static Device fromMap(Map<String, Object> map) {
        Device d = new Device();
        if (map.containsKey("state")) d.setState((Boolean) map.get("state"));
        if (map.containsKey("brightness")) d.setBrightness((Integer) map.get("brightness"));
        if (map.containsKey("color")) d.setColor((String) map.get("color"));
        if (map.containsKey("temp")) d.setTemp((Integer) map.get("temp"));
        if (map.containsKey("mode")) d.setMode((String) map.get("mode"));
        if (map.containsKey("openPercent")) d.setOpenPercent((Integer) map.get("openPercent"));
        if (map.containsKey("volume")) d.setVolume((Integer) map.get("volume"));
        if (map.containsKey("input")) d.setInput((String) map.get("input"));
        if (map.containsKey("speed")) d.setSpeed((Integer) map.get("speed"));
        if (map.containsKey("locked")) d.setLocked((Boolean) map.get("locked"));
        if (map.containsKey("tempSet")) d.setTempSet((Integer) map.get("tempSet"));
        if (map.containsKey("recording")) d.setRecording((Boolean) map.get("recording"));
        if (map.containsKey("playing")) d.setPlaying((String) map.get("playing"));
        return d;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isState() { return state; }
    public void setState(boolean state) { this.state = state; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getIconBg() { return iconBg; }
    public void setIconBg(String iconBg) { this.iconBg = iconBg; }
    public Integer getPower() { return power; }
    public void setPower(Integer power) { this.power = power; }
    public Integer getBrightness() { return brightness; }
    public void setBrightness(Integer brightness) { this.brightness = brightness; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getTemp() { return temp; }
    public void setTemp(Integer temp) { this.temp = temp; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public Integer getOpenPercent() { return openPercent; }
    public void setOpenPercent(Integer openPercent) { this.openPercent = openPercent; }
    public Integer getVolume() { return volume; }
    public void setVolume(Integer volume) { this.volume = volume; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }
    public Boolean getLocked() { return locked; }
    public void setLocked(Boolean locked) { this.locked = locked; }
    public Integer getTempSet() { return tempSet; }
    public void setTempSet(Integer tempSet) { this.tempSet = tempSet; }
    public Boolean getRecording() { return recording; }
    public void setRecording(Boolean recording) { this.recording = recording; }
    public String getPlaying() { return playing; }
    public void setPlaying(String playing) { this.playing = playing; }
    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    public Integer getWaterLevel() { return waterLevel; }
    public void setWaterLevel(Integer waterLevel) { this.waterLevel = waterLevel; }
    public Integer getBattery() { return battery; }
    public void setBattery(Integer battery) { this.battery = battery; }
    public Boolean getAlarm() { return alarm; }
    public void setAlarm(Boolean alarm) { this.alarm = alarm; }
    public Integer getTempFridge() { return tempFridge; }
    public void setTempFridge(Integer tempFridge) { this.tempFridge = tempFridge; }
    public Integer getTempFreezer() { return tempFreezer; }
    public void setTempFreezer(Integer tempFreezer) { this.tempFreezer = tempFreezer; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    public String getLastUnlock() { return lastUnlock; }
    public void setLastUnlock(String lastUnlock) { this.lastUnlock = lastUnlock; }
    public String getLastEvent() { return lastEvent; }
    public void setLastEvent(String lastEvent) { this.lastEvent = lastEvent; }
    public Boolean getClosed() { return closed; }
    public void setClosed(Boolean closed) { this.closed = closed; }
}
