package com.producer.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class ProducerDTO {
    private static final String DATE_TIME_FORMAT = "d-MMM-uuuu HH:mm:ss";
    private static final String CLASS_FORMAT = "ProducerDTO(Timestamp = %s, Device ID = %s, Measurement Value = %f)";
    private long timestamp;
    private String deviceId;
    private Double measurementValue;

//    @Override
//    public String toString() {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
//        String localDateTime = Instant
//                .ofEpochMilli(timestamp)
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime()
//                .format(dateTimeFormatter);
//        return String.format(CLASS_FORMAT, localDateTime, deviceId, measurementValue);
//    }


}

