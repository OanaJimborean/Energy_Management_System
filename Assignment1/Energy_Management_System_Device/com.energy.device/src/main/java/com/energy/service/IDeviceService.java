package com.energy.service;

import com.energy.dto.DeviceDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IDeviceService {
    ResponseEntity<List<DeviceDTO>> getAllDevices();

    ResponseEntity<String> addNewDevice(Map<String, String> requestMap);

    ResponseEntity<String> deleteDevice(Integer id);

    ResponseEntity<String> updateDevice(Map<String, String> requestMap);

    ResponseEntity<String> deleteDeviceByUserId(Integer id);

    ResponseEntity<List<DeviceDTO>> getByUserId(Integer id);
}
