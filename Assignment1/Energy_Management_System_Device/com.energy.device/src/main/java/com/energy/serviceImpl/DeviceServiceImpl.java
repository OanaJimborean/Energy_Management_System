package com.energy.serviceImpl;

import com.energy.constents.Constants;
import com.energy.dao.IDeviceDAO;
import com.energy.dto.DeviceDTO;
import com.energy.model.Device;
import com.energy.security.JwtFilter;
import com.energy.service.IDeviceService;
import com.energy.utils.EnergyUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
public class DeviceServiceImpl implements IDeviceService {

    @Autowired
    IDeviceDAO deviceDAO;

    @Autowired
    JwtFilter jwtFilter;


    @Override
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        try {
            return new ResponseEntity<>(deviceDAO.getAllDevices(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> addNewDevice(Map<String, String> requestMap) {
        try{
            if (validateDeviceMapForAdd(requestMap)) {
                deviceDAO.save(getDeviceFromMap(requestMap, false));
                return EnergyUtility.getResponseEntity("Device Added Successfully", HttpStatus.OK);
            }
            return EnergyUtility.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteDevice(Integer id) {
        try{
            Optional optional = deviceDAO.findById(id);
            if(!optional.isEmpty()){
                deviceDAO.deleteById(id);
                return EnergyUtility.getResponseEntity("Device Deleted Successfully", HttpStatus.OK);
            }else{
                return EnergyUtility.getResponseEntity("Device id does not exist", HttpStatus.OK);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateDevice(Map<String, String> requestMap) {
        try{
            if (validateDeviceMap(requestMap, true)) {
                Optional<Device> optional = deviceDAO.findById(Integer.parseInt(requestMap.get("deviceId")));
                if (!optional.isEmpty()) {
                    Device device = getDeviceFromMapForUpdate(requestMap, true);
                    deviceDAO.save(device);
                    return EnergyUtility.getResponseEntity("Device Updated Successfully", HttpStatus.OK);
                } else {
                    return EnergyUtility.getResponseEntity("Device id does not exist", HttpStatus.OK);
                }
            } else {
                return EnergyUtility.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteDeviceByUserId(Integer userId) {
        try {
            List<Device> devices = deviceDAO.findDevicesByUserId(userId);
            if (!devices.isEmpty()) {
                // Delete all devices associated with the user
                deviceDAO.deleteAll(devices);
                return EnergyUtility.getResponseEntity("Devices Deleted Successfully", HttpStatus.OK);
            } else {
                return EnergyUtility.getResponseEntity("No devices found for the user", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<DeviceDTO>> getByUserId(Integer userId) {
        if(jwtFilter.isUser() || jwtFilter.isAdmin()) {
            try {
                List<DeviceDTO> devices = deviceDAO.getDeviceByUserId(userId);
                return new ResponseEntity<>(devices, HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace(); // Log the exception for debugging purposes
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.FORBIDDEN); // Or appropriate status code for unauthorized access
        }
    }



    private Device getDeviceFromMapForUpdate(Map<String, String> requestMap, boolean isAdd) {
        Device device = new Device();
        if(isAdd){
            device.setDeviceId(Integer.parseInt(requestMap.get("deviceId")));
        }

        device.setDevice_name(requestMap.get("device_name"));
        device.setAddress(requestMap.get("address"));
        device.setDescription(requestMap.get("description"));
        device.setMax_hourly(Float.parseFloat(requestMap.get("max_hourly")));
        device.setUserId(Integer.valueOf(requestMap.get("userId")));

        return device;
    }

    private Device getDeviceFromMap(Map<String, String> requestMap, boolean isAdd) {
        Device device = new Device();
        if(isAdd){
            device.setDeviceId(Integer.parseInt(requestMap.get("deviceId")));
        }

        device.setDevice_name(requestMap.get("device_name"));
        device.setAddress(requestMap.get("address"));
        device.setDescription(requestMap.get("description"));
        device.setMax_hourly(Float.parseFloat(requestMap.get("max_hourly")));
        //device.setUserId(Integer.valueOf(requestMap.get("userId")));

        if (requestMap.containsKey("userId")) {
            String userId = requestMap.get("userId");
            if (userId.isEmpty()) {
                device.setUserId(null);
            } else {
                device.setUserId(Integer.valueOf(userId));
            }
        } else {
            device.setUserId(null);
        }
        return device;
    }

    private boolean validateDeviceMapForAdd(Map<String, String> requestMap) {
        if(requestMap.containsKey("device_name") && requestMap.containsKey("address") && requestMap.containsKey("description") && requestMap.containsKey("max_hourly") && requestMap.containsKey("userId")){
                return true;
        }
        return false;
    }

    private boolean validateDeviceMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("device_name") && requestMap.containsKey("address") && requestMap.containsKey("description") && requestMap.containsKey("max_hourly") && requestMap.containsKey("userId")){
            if(requestMap.containsKey("deviceId") && validateId){
                return true;
            }
            else return !validateId;
        }
        return false;
    }
}
