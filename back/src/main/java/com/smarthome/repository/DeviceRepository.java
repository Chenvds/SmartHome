package com.smarthome.repository;

import com.smarthome.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
    Optional<DeviceEntity> findByDeviceId(String deviceId);
    List<DeviceEntity> findAllByOrderByDeviceId();
    boolean existsByDeviceId(String deviceId);
    List<DeviceEntity> findByRoomOrderByDeviceId(String room);
}
