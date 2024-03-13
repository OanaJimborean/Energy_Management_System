package com.energy.restImpl;

import com.energy.constents.Constants;
import com.energy.dto.DeviceDTO;
import com.energy.rest.IDeviceRest;
import com.energy.service.IDeviceService;
import com.energy.utils.EnergyUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class DeviceRestImpl implements IDeviceRest {

    @Autowired
    IDeviceService deviceService;

    @Override
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        try{
            return deviceService.getAllDevices();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> addNewDevice(Map<String, String> requestMap) {
        try{
            return deviceService.addNewDevice(requestMap);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteDevice(Integer id) {
        try{
            return deviceService.deleteDevice(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateDevice(Map<String, String> requestMap) {
        try{
            return deviceService.updateDevice(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteByUserId(Integer id) {
        try{
            return deviceService.deleteDeviceByUserId(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<DeviceDTO>> getByUsers(Integer id) {
        try{
            return deviceService.getByUserId(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}



