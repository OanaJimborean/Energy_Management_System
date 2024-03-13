package com.energy.service;

import com.energy.dto.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IUserService {

    ResponseEntity<String> signUp(Map<String, String> requestMap);
    ResponseEntity<String> login(Map<String, String> requestMap);
    ResponseEntity<List<UserDTO>> getAllUser();
    ResponseEntity<String> deleteUser(Integer id);
    ResponseEntity<String> updateUser(Map<String, String> requestMap);

    boolean existsById(Integer id);

    ResponseEntity<List<UserDTO>> getLoggedInUsers();
}
