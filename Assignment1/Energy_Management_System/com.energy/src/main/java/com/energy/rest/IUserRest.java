package com.energy.rest;

import com.energy.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://192.168.0.241:3000")
@RequestMapping(path = "/user")
public interface IUserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/getUsers")
    ResponseEntity<List<UserDTO>> getAllUser();

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteUser(@PathVariable Integer id);

    @PostMapping(path = "/update")
    ResponseEntity<String> updateUser(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/checkUserExistence/{id}")
    ResponseEntity<String> checkUserExists(@PathVariable Integer id);

    @GetMapping(path = "/getLoggedInUsers")
    ResponseEntity<List<UserDTO>> getLoggedInUsers();




}
