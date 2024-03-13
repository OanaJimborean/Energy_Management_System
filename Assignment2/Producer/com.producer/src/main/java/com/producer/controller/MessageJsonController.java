package com.producer.controller;

import com.producer.dto.ProducerDTO;
import com.producer.publisher.RabbitMQJsonProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class MessageJsonController {

    private RabbitMQJsonProducer jsonProducer;
    private int messageCounter = 0;
    private String deviceId = UUID.randomUUID().toString();

    public MessageJsonController(RabbitMQJsonProducer jsonProducer) {
        this.jsonProducer = jsonProducer;
    }

    @GetMapping("/publish")
    public ResponseEntity<String> sendMessage() {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader("sensor.csv"));
            String row;
            Thread.sleep(2000);
            while ((row = csvReader.readLine()) != null) {

                Double measurementValue = Double.parseDouble(row.trim());

                long currentTimeMillis = Instant.now().toEpochMilli();

                if (messageCounter == 6) {
                    deviceId = UUID.randomUUID().toString();
                    messageCounter = 0;
                }
                messageCounter++;
                ProducerDTO producerDTO = new ProducerDTO();
                producerDTO.setTimestamp(currentTimeMillis);
                producerDTO.setDeviceId(deviceId);
                producerDTO.setMeasurementValue(measurementValue);

                jsonProducer.sendJsonMessage(producerDTO);

                Thread.sleep(5000);
            }
            csvReader.close();
        } catch (IOException | InterruptedException | NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending messages");
        }

        return ResponseEntity.ok("Messages sent");
    }
}
