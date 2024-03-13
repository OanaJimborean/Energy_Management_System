package com.energy.dao;

import com.energy.dto.UserDTO;
import com.energy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserDAO extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);
    List<UserDTO> getAllUser();

    List<User> findByLastLoginTimeAfter(LocalDateTime oneHourAgo);
}
