package com.energy.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NamedQuery(name = "User.findByEmailId", query = "select u from User u where u.email=:email")
@NamedQuery(name = "User.getAllUser", query = "select new com.energy.dto.UserDTO(u.userId, u.username, u.email, u.role) from User u ")
@NamedQuery(name = "User.findByLastLoginTimeAfter", query = "SELECT u FROM User u WHERE u.lastLoginTime > :oneHourAgo")

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID=1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "userid")
    private Integer userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private String status;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;


}
