package com.energy.model;

import lombok.Data;
import org.apache.tomcat.jni.User;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NamedQuery(name = "Device.getAllDevices", query = "select new com.energy.dto.DeviceDTO(d.deviceId, d.device_name,d.address, d.description, d.userId, d.max_hourly) from Device d")
@NamedQuery(name = "Device.deleteDeviceByUserId", query = "delete from Device d where d.userId=:userId")
@NamedQuery(name = "Device.getDeviceByUserId", query = "select new com.energy.dto.DeviceDTO(d.deviceId, d.device_name,d.address, d.description, d.userId, d.max_hourly) from Device d where d.userId=:id")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "device")
public class Device implements Serializable {

    private static final long serialVersionUID=1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "deviceid")
    private Integer deviceId;

    @Column(name = "device_name")
    private String device_name;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "max_hourly")
    private float max_hourly;

    @Column(name = "userid")
    private Integer userId;

}
