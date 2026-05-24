package com.smarthome.service;

import com.smarthome.entity.DeviceEntity;
import com.smarthome.model.Device;
import com.smarthome.model.Room;
import com.smarthome.repository.DeviceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final Map<String, Device> cache = new ConcurrentHashMap<>();
    private final Map<String, Room> roomCache = new LinkedHashMap<>();
    // Room registry: stores room metadata for rooms without devices
    private final Map<String, Room> roomRegistry = new ConcurrentHashMap<>();

    // Known room name -> ID mapping
    private static final Map<String, String> KNOWN_ROOMS = Map.of(
        "客厅", "living", "卧室", "bedroom", "厨房", "kitchen",
        "卫生间", "bathroom", "门禁", "door"
    );

    // Reverse: ID -> default display name
    private static final Map<String, String> KNOWN_IDS = Map.of(
        "living", "客厅", "bedroom", "卧室", "kitchen", "厨房",
        "bathroom", "卫生间", "door", "门禁"
    );

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @PostConstruct
    public void loadCache() {
        cache.clear();
        roomCache.clear();
        List<DeviceEntity> entities = deviceRepository.findAllByOrderByDeviceId();
        if (entities.isEmpty()) return;

        Map<String, List<Device>> roomDevices = new LinkedHashMap<>();
        for (DeviceEntity e : entities) {
            Device d = e.toDevice();
            cache.put(d.getId(), d);
            roomDevices.computeIfAbsent(e.getRoom(), k -> new ArrayList<>()).add(d);
        }

        Set<String> seenRooms = new LinkedHashSet<>();
        for (DeviceEntity e : entities) {
            String roomKey = e.getRoom();
            if (!seenRooms.contains(roomKey)) {
                seenRooms.add(roomKey);
                Room r = new Room();
                r.setId(getRoomId(roomKey));
                r.setName(roomKey);
                r.setIcon(e.getRoomIcon());
                r.setDesc(e.getRoomDesc());
                r.setIconClass(e.getRoomIconClass());
                r.setDevices(roomDevices.getOrDefault(roomKey, new ArrayList<>()));
                roomCache.put(r.getId(), r);
            }
        }
    }

    public List<Room> getAllRooms() {
        if (roomCache.isEmpty() && cache.isEmpty() && entityCount() > 0) loadCache();
        // Merge roomCache with roomRegistry
        Map<String, Room> all = new LinkedHashMap<>(roomCache);
        for (Room r : roomRegistry.values()) {
            if (!all.containsKey(r.getId())) {
                r.setDevices(new ArrayList<>());
                all.put(r.getId(), r);
            }
        }
        return new ArrayList<>(all.values());
    }

    public List<Device> getAllDevices() {
        if (cache.isEmpty() && entityCount() > 0) loadCache();
        return new ArrayList<>(cache.values());
    }

    public Device getDevice(String id) {
        if (cache.isEmpty() && entityCount() > 0) loadCache();
        return cache.get(id);
    }

    // ── Room CRUD ──

    @Transactional
    public Room addRoom(String name, String icon, String desc, String iconClass) {
        String id = generateRoomId(name);
        // If room exists in cache, just return it
        if (roomCache.containsKey(id)) return roomCache.get(id);
        if (roomRegistry.containsKey(id)) return roomRegistry.get(id);

        Room r = new Room();
        r.setId(id);
        r.setName(name);
        r.setIcon(icon != null ? icon : "📦");
        r.setDesc(desc != null ? desc : "");
        r.setIconClass(iconClass != null ? iconClass : "default");
        r.setDevices(new ArrayList<>());
        roomRegistry.put(id, r);
        return r;
    }

    @Transactional
    public boolean deleteRoom(String roomId) {
        // Find room by ID in cache or registry
        Room room = roomCache.get(roomId);
        if (room == null) room = roomRegistry.get(roomId);
        if (room == null) return false;

        // Delete all devices in this room
        String roomName = room.getName();
        List<Device> roomDevices = cache.values().stream()
            .filter(d -> roomName.equals(d.getRoom()))
            .collect(Collectors.toList());

        for (Device d : roomDevices) {
            cache.remove(d.getId());
            deviceRepository.findByDeviceId(d.getId()).ifPresent(deviceRepository::delete);
        }

        roomCache.remove(roomId);
        roomRegistry.remove(roomId);
        return true;
    }

    // ── Device CRUD ──

    @Transactional
    public Device addDevice(String roomId, Map<String, Object> props) {
        // Resolve room
        Room room = roomCache.get(roomId);
        if (room == null) room = roomRegistry.get(roomId);
        if (room == null) throw new IllegalArgumentException("房间不存在: " + roomId);

        String roomName = room.getName();
        String deviceId = generateDeviceId(roomId, (String) props.get("name"));

        Device d = new Device();
        d.setId(deviceId);
        d.setName((String) props.getOrDefault("name", "新设备"));
        d.setIcon((String) props.getOrDefault("icon", "📡"));
        d.setType((String) props.getOrDefault("type", "other"));
        d.setState(Boolean.TRUE.equals(props.get("state")));
        d.setRoom(roomName);
        d.setIconBg((String) props.getOrDefault("iconBg", "pb"));
        if (props.containsKey("power")) d.setPower(toInt(props.get("power")));

        // Save to DB
        DeviceEntity e = DeviceEntity.fromDevice(d, roomName, room.getIcon(),
            room.getDesc(), room.getIconClass());
        deviceRepository.save(e);

        // Update cache
        cache.put(deviceId, d);
        room.getDevices().add(d);
        roomCache.put(roomId, room);

        return d;
    }

    @Transactional
    public boolean deleteDevice(String deviceId) {
        Device d = cache.remove(deviceId);
        if (d == null) return false;
        deviceRepository.findByDeviceId(deviceId).ifPresent(deviceRepository::delete);
        // Remove from room device list
        for (Room r : roomCache.values()) {
            r.getDevices().removeIf(dd -> dd.getId().equals(deviceId));
        }
        return true;
    }

    // ── Toggle / Update ──

    @Transactional
    public Device toggleDevice(String id) {
        Device d = getDevice(id);
        if (d == null) return null;
        d.setState(!d.isState());
        saveDeviceToDb(id);
        return d;
    }

    @Transactional
    public Device updateDevice(String id, Map<String, Object> updates) {
        Device d = getDevice(id);
        if (d == null) return null;
        applyUpdates(d, updates);
        saveDeviceToDb(id);
        return d;
    }

    @Transactional
    public Device updateDeviceAll(String id, Map<String, Object> props) {
        Device d = getDevice(id);
        if (d == null) return null;
        d.setName((String) props.getOrDefault("name", d.getName()));
        d.setIcon((String) props.getOrDefault("icon", d.getIcon()));
        d.setType((String) props.getOrDefault("type", d.getType()));
        d.setIconBg((String) props.getOrDefault("iconBg", d.getIconBg()));
        if (props.containsKey("power")) d.setPower(toInt(props.get("power")));
        applyUpdates(d, props);
        saveDeviceToDb(id);
        return d;
    }

    private void applyUpdates(Device d, Map<String, Object> u) {
        if (u.containsKey("state")) d.setState((Boolean) u.get("state"));
        if (u.containsKey("brightness")) d.setBrightness(toInt(u.get("brightness")));
        if (u.containsKey("color")) d.setColor((String) u.get("color"));
        if (u.containsKey("temp")) d.setTemp(toInt(u.get("temp")));
        if (u.containsKey("mode")) d.setMode((String) u.get("mode"));
        if (u.containsKey("openPercent")) d.setOpenPercent(toInt(u.get("openPercent")));
        if (u.containsKey("volume")) d.setVolume(toInt(u.get("volume")));
        if (u.containsKey("input")) d.setInput((String) u.get("input"));
        if (u.containsKey("speed")) d.setSpeed(toInt(u.get("speed")));
        if (u.containsKey("locked")) d.setLocked((Boolean) u.get("locked"));
        if (u.containsKey("tempSet")) d.setTempSet(toInt(u.get("tempSet")));
        if (u.containsKey("recording")) d.setRecording((Boolean) u.get("recording"));
        if (u.containsKey("playing")) d.setPlaying((String) u.get("playing"));
    }

    private void saveDeviceToDb(String deviceId) {
        Device d = cache.get(deviceId);
        if (d == null) return;
        for (Room r : roomCache.values()) {
            if (r.getDevices().stream().anyMatch(dd -> dd.getId().equals(deviceId))) {
                DeviceEntity e = DeviceEntity.fromDevice(d, r.getName(), r.getIcon(),
                    r.getDesc(), r.getIconClass());
                deviceRepository.findByDeviceId(deviceId).ifPresentOrElse(
                    existing -> { e.setId(existing.getId()); deviceRepository.save(e); },
                    () -> deviceRepository.save(e)
                );
                return;
            }
        }
    }

    private Integer toInt(Object v) {
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) try { return Integer.parseInt((String) v); } catch (Exception e) { return null; }
        return null;
    }

    private long entityCount() {
        try { return deviceRepository.count(); } catch (Exception e) { return 0; }
    }

    private String generateRoomId(String name) {
        // Try known mapping first
        if (KNOWN_ROOMS.containsKey(name)) return KNOWN_ROOMS.get(name);
        // Generate from name: pinyin-like transliteration
        String base = name.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-");
        if (base.isEmpty()) base = "room";
        String id = base;
        int suffix = 1;
        while (roomCache.containsKey(id) || roomRegistry.containsKey(id)) {
            id = base + "-" + (suffix++);
        }
        return id;
    }

    private String generateDeviceId(String roomId, String deviceName) {
        String base = roomId + "-" + (deviceName != null
            ? deviceName.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-")
            : "device");
        if (base.endsWith("-")) base = base.substring(0, base.length() - 1);
        String id = base;
        int suffix = 1;
        while (cache.containsKey(id)) {
            id = base + "-" + (suffix++);
        }
        return id;
    }

    public static String getRoomId(String roomName) {
        return KNOWN_ROOMS.getOrDefault(roomName, roomName);
    }
}
