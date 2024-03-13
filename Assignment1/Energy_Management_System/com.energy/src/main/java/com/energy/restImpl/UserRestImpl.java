package com.energy.restImpl;

import com.energy.constents.Constants;
import com.energy.dao.IUserDAO;
import com.energy.dto.UserDTO;
import com.energy.model.User;
import com.energy.rest.IUserRest;
import com.energy.service.IUserService;
import com.energy.utils.EnergyUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class UserRestImpl implements IUserRest {
    @Autowired
    IUserService userService;

    @Autowired
    IUserDAO userDAO;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUser() {
        try{
            return userService.getAllUser();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<String> deleteUser(Integer id) {
        try{
            return userService.deleteUser(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try{
            return userService.updateUser(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkUserExists(Integer id) {
            if (userService.existsById(id)) {
                return new ResponseEntity<>("User with the specified ID exists", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User with the specified ID does not exist", HttpStatus.NOT_FOUND);
            }
        }

    @Override
    public ResponseEntity<List<UserDTO>> getLoggedInUsers() {
        try{
            return userService.getLoggedInUsers();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}


