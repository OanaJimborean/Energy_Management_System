package com.energy.userImpl;

import com.energy.JWT.CustomerUserDetailsServ;
import com.energy.JWT.JwtFilter;
import com.energy.JWT.JwtUtil;
import com.energy.constents.Constants;
import com.energy.dao.IUserDAO;
import com.energy.dto.UserDTO;
import com.energy.model.User;
import com.energy.service.IUserService;
import com.energy.utils.EnergyUtility;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    IUserDAO userDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsServ customerUserDetailsServ;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDAO.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDAO.save(getUserFromMap(requestMap));
                    return EnergyUtility.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return EnergyUtility.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return EnergyUtility.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                // Retrieve the user
                User user = userDAO.findByEmailId(requestMap.get("email"));

                user.setLastLoginTime(LocalDateTime.now());
                userDAO.save(user);

                return new ResponseEntity<String>("{\"token\":\"" +
                        jwtUtil.generateToken(customerUserDetailsServ.getUserDetail().getEmail(),
                                customerUserDetailsServ.getUserDetail().getRole(), customerUserDetailsServ.getUserDetail().getUserId()) + "\"}",
                        HttpStatus.OK);
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad credentials." + "\"}",
                HttpStatus.BAD_REQUEST);
    }


    @Override
    public ResponseEntity<List<UserDTO>> getAllUser() {
        try {
            if(jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDAO.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteUser(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional optional =  userDAO.findById(id);
                if(!optional.isEmpty()){
                    userDAO.deleteById(id);
                    return EnergyUtility.getResponseEntity("User Deleted Successfully", HttpStatus.OK);
                }else{
                    return EnergyUtility.getResponseEntity("User id does not exist", HttpStatus.OK);
                }
            }else{
                return EnergyUtility.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateUserMap(requestMap, true)){
                    Optional<User> optional = userDAO.findById(Integer.parseInt(requestMap.get("userId")));
                    if(!optional.isEmpty()){
                        User user = getUserFromMapForUpdate(requestMap, true);
                        user.setStatus(optional.get().getStatus());
                        user.setPassword(optional.get().getPassword());
                        userDAO.save(user);
                        return EnergyUtility.getResponseEntity("User Updated Successfully", HttpStatus.OK);
                    }else{
                        return EnergyUtility.getResponseEntity("User id does not exist", HttpStatus.OK);
                    }
                }else{
                    return EnergyUtility.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }else{
                return EnergyUtility.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return EnergyUtility.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean existsById(Integer id) {
        return userDAO.existsById(id);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getLoggedInUsers() {
        try {
            if (jwtFilter.isUser() || jwtFilter.isAdmin()) {
                LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
                List<User> loggedInUsers = userDAO.findByLastLoginTimeAfter(oneHourAgo);
                List<UserDTO> loggedInUserDTOs = convertToUserDTO(loggedInUsers);
                return new ResponseEntity<>(loggedInUserDTOs, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public List<UserDTO> convertToUserDTO(List<User> userList) {
        return userList.stream()
                .map(user -> new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }



    private User getUserFromMapForUpdate(Map<String, String> requestMap, boolean isAdd) {

        User user = new User();
        if(isAdd){
            user.setUserId(Integer.parseInt(requestMap.get("userId")));
        }else{
            user.setStatus("true");
        }

        user.setUsername(requestMap.get("username"));
        user.setRole(requestMap.get("role"));
        user.setEmail(requestMap.get("email"));
        //user.setPassword(requestMap.get("password"));

        return user;
    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("username") && requestMap.containsKey("password") && requestMap.containsKey("email") && requestMap.containsKey("role")) {
            return true;
        }
        return false;

    }

    private boolean validateUserMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("username") && requestMap.containsKey("role") && requestMap.containsKey("email")){
            if(requestMap.containsKey("userId") && validateId){
                return true;
            }
            else return !validateId;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setUsername(requestMap.get("username"));
        user.setPassword(requestMap.get("password"));
        user.setEmail(requestMap.get("email"));
        user.setStatus("false");
        user.setRole(requestMap.get("role"));

        return user;

    }


}
