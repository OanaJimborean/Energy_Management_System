package com.energy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceDTO {
    private Integer deviceId;
    private String device_name;
    private String address;
    private String description;
    private Integer userId;
    private Float max_hourly;

    public DeviceDTO(Integer deviceId, String device_name, String address, String description, Integer userId, float max_hourly) {
        this.deviceId = deviceId;
        this.device_name = device_name;
        this.address = address;
        this.description = description;
        this.userId = userId;
        this.max_hourly = max_hourly;
    }
}
