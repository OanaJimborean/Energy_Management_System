package com.energy.rest;

import com.energy.dto.DeviceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path = "/device")
public interface IDeviceRest {

    @GetMapping(path = "/getDevices")
    ResponseEntity<List<DeviceDTO>> getAllDevices();

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewDevice(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteDevice(@PathVariable Integer id);

    @PostMapping(path = "/update")
    ResponseEntity<String> updateDevice(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/deleteByUserId/{id}")
    ResponseEntity<String> deleteByUserId(@PathVariable Integer id);

    @GetMapping(path = "/getDevicesByUserId/{id}")
    ResponseEntity<List<DeviceDTO>> getByUsers(@PathVariable Integer id);

}
