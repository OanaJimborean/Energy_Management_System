package com.energy.dao;

import com.energy.dto.DeviceDTO;
import com.energy.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface IDeviceDAO extends JpaRepository<Device, Integer> {
    List<DeviceDTO> getAllDevices();
    List<Device> findDevicesByUserId(Integer userId);
    List<DeviceDTO> getDeviceByUserId(@Param("id") Integer userId);

}
