package com.consumer.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "monitoring")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer id;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "deviceid")
    private String deviceId;

    @Column(name = "measurement_value")
    private Double measurementValue;

    @Column(name = "hourly_consumption")
    private Double hourlyConsumption;

}
