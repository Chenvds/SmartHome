package com.smarthome.controller;

import com.smarthome.dto.ApiResponse;
import com.smarthome.exception.DeviceNotFoundException;
import com.smarthome.model.Device;
import com.smarthome.model.Room;
import com.smarthome.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    // ── Rooms ──

    @GetMapping("/rooms")
    public ApiResponse<List<Room>> getRooms() {
        return ApiResponse.ok(service.getAllRooms());
    }

    @PostMapping("/rooms")
    public ApiResponse<Room> addRoom(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) return ApiResponse.fail("房间名称不能为空");
        Room r = service.addRoom(name, (String) body.get("icon"),
            (String) body.get("desc"), (String) body.get("iconClass"));
        return ApiResponse.ok("房间已创建", r);
    }

    @DeleteMapping("/rooms/{id}")
    public ApiResponse<Void> deleteRoom(@PathVariable String id) {
        if (service.deleteRoom(id)) return ApiResponse.ok("房间已删除", null);
        return ApiResponse.fail("房间未找到");
    }

    // ── Devices ──

    @GetMapping("/devices")
    public ApiResponse<List<Device>> getDevices() {
        return ApiResponse.ok(service.getAllDevices());
    }

    @GetMapping("/devices/{id}")
    public ApiResponse<Device> getDevice(@PathVariable String id) {
        Device d = service.getDevice(id);
        if (d == null) throw new DeviceNotFoundException(id);
        return ApiResponse.ok(d);
    }

    @PostMapping("/devices")
    public ApiResponse<Device> addDevice(@RequestBody Map<String, Object> body) {
        String roomId = (String) body.get("roomId");
        if (roomId == null || roomId.isBlank()) return ApiResponse.fail("必须指定房间");
        try {
            Device d = service.addDevice(roomId, body);
            return ApiResponse.ok("设备已添加", d);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/devices/{id}/toggle")
    public ApiResponse<Device> toggleDevice(@PathVariable String id) {
        Device d = service.toggleDevice(id);
        if (d == null) throw new DeviceNotFoundException(id);
        return ApiResponse.ok(d.isState() ? "已开启" : "已关闭", d);
    }

    @PutMapping("/devices/{id}")
    public ApiResponse<Device> updateDevice(@PathVariable String id,
                                            @RequestBody Map<String, Object> body) {
        Device d = service.updateDevice(id, body);
        if (d == null) throw new DeviceNotFoundException(id);
        return ApiResponse.ok("已更新", d);
    }

    @DeleteMapping("/devices/{id}")
    public ApiResponse<Void> deleteDevice(@PathVariable String id) {
        if (service.deleteDevice(id)) return ApiResponse.ok("设备已删除", null);
        return ApiResponse.fail("设备未找到");
    }
}
