package com.smarthome.entity;

import com.smarthome.model.Device;
import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String deviceId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String icon;

    @Column(length = 30)
    private String type;

    private boolean state;

    @Column(length = 30)
    private String room;

    @Column(length = 20)
    private String iconBg;

    private Integer power;
    private Integer brightness;

    @Column(length = 10)
    private String color;

    private Integer temp;

    @Column(length = 20)
    private String mode;

    private Integer openPercent;
    private Integer volume;

    @Column(name = "input_source", length = 20)
    private String input;

    private Integer speed;
    private Boolean locked;
    private Integer tempSet;
    private Boolean recording;

    @Column(length = 50)
    private String playing;

    private Integer humidity;
    private Integer waterLevel;
    private Integer battery;
    private Boolean alarm;
    private Integer tempFridge;
    private Integer tempFreezer;

    @Column(length = 20)
    private String resolution;

    @Column(length = 50)
    private String storage;

    @Column(length = 50)
    private String lastUnlock;

    @Column(length = 100)
    private String lastEvent;

    private Boolean closed;

    // Room metadata (denormalized)
    @Column(length = 20)
    private String roomIcon;

    @Column(length = 50)
    private String roomDesc;

    @Column(length = 20)
    private String roomIconClass;

    public DeviceEntity() {}

    // Convert from model Device + room info
    public static DeviceEntity fromDevice(Device d, String roomName, String roomIcon, String roomDesc, String roomIconClass) {
        DeviceEntity e = new DeviceEntity();
        e.deviceId = d.getId();
        e.name = d.getName();
        e.icon = d.getIcon();
        e.type = d.getType();
        e.state = d.isState();
        e.room = d.getRoom();
        e.iconBg = d.getIconBg();
        e.power = d.getPower();
        e.brightness = d.getBrightness();
        e.color = d.getColor();
        e.temp = d.getTemp();
        e.mode = d.getMode();
        e.openPercent = d.getOpenPercent();
        e.volume = d.getVolume();
        e.input = d.getInput();
        e.speed = d.getSpeed();
        e.locked = d.getLocked();
        e.tempSet = d.getTempSet();
        e.recording = d.getRecording();
        e.playing = d.getPlaying();
        e.humidity = d.getHumidity();
        e.waterLevel = d.getWaterLevel();
        e.battery = d.getBattery();
        e.alarm = d.getAlarm();
        e.tempFridge = d.getTempFridge();
        e.tempFreezer = d.getTempFreezer();
        e.resolution = d.getResolution();
        e.storage = d.getStorage();
        e.lastUnlock = d.getLastUnlock();
        e.lastEvent = d.getLastEvent();
        e.closed = d.getClosed();
        e.roomIcon = roomIcon;
        e.roomDesc = roomDesc;
        e.roomIconClass = roomIconClass;
        return e;
    }

    // Convert to model Device
    public Device toDevice() {
        Device d = new Device();
        d.setId(deviceId);
        d.setName(name);
        d.setIcon(icon);
        d.setType(type);
        d.setState(state);
        d.setRoom(room);
        d.setIconBg(iconBg);
        d.setPower(power);
        d.setBrightness(brightness);
        d.setColor(color);
        d.setTemp(temp);
        d.setMode(mode);
        d.setOpenPercent(openPercent);
        d.setVolume(volume);
        d.setInput(input);
        d.setSpeed(speed);
        d.setLocked(locked);
        d.setTempSet(tempSet);
        d.setRecording(recording);
        d.setPlaying(playing);
        d.setHumidity(humidity);
        d.setWaterLevel(waterLevel);
        d.setBattery(battery);
        d.setAlarm(alarm);
        d.setTempFridge(tempFridge);
        d.setTempFreezer(tempFreezer);
        d.setResolution(resolution);
        d.setStorage(storage);
        d.setLastUnlock(lastUnlock);
        d.setLastEvent(lastEvent);
        d.setClosed(closed);
        return d;
    }

    // Getters & Setters (for JPA)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
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
    public String getRoomIcon() { return roomIcon; }
    public void setRoomIcon(String roomIcon) { this.roomIcon = roomIcon; }
    public String getRoomDesc() { return roomDesc; }
    public void setRoomDesc(String roomDesc) { this.roomDesc = roomDesc; }
    public String getRoomIconClass() { return roomIconClass; }
    public void setRoomIconClass(String roomIconClass) { this.roomIconClass = roomIconClass; }
}
