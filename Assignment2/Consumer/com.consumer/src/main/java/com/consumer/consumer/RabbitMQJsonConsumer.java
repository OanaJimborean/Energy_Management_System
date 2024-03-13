package com.consumer.consumer;

import com.consumer.dao.IMessageDAO;
import com.consumer.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RabbitMQJsonConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonConsumer.class);
    private static final String DATE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";

    @Autowired
    private IMessageDAO IMessageDAO;

    private Map<Integer, Double> hourlyConsumption = new HashMap<>();
    private int messageCount = 0;
    private final int RESET_COUNT = 6;

    private double currentHourlyConsumption = 0.0;

    @RabbitListener(queues = {"${rabbitmq.queue.json.name}"})
    public void consumeJsonMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(message);

            long timestamp = jsonNode.get("timestamp").asLong();
            Double measurementValue = Double.parseDouble(jsonNode.get("measurementValue").asText());
            String deviceId = jsonNode.get("deviceId").asText();

            int idDevice = convertUUIDToInt(deviceId);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
            String localDateTime = Instant
                    .ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .plusHours(2)
                    .toLocalDateTime()
                    .format(dateTimeFormatter);

            LocalDateTime dateTime = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            int hour = dateTime.getHour();

            updateHourlyConsumption(hour, measurementValue);

            currentHourlyConsumption += measurementValue;

            if (messageCount == RESET_COUNT) {
                messageCount = 0;

                hourlyConsumption.clear();
                currentHourlyConsumption = measurementValue;
                updateHourlyConsumption(hour, measurementValue);
            }messageCount++;


            Message message1 = new Message();
            message1.setTimestamp(localDateTime);
            message1.setDeviceId(String.valueOf(idDevice));
            message1.setMeasurementValue(measurementValue);
            message1.setHourlyConsumption(hourlyConsumption.get(hour));

            IMessageDAO.save(message1);

            LOGGER.info("Data saved to the database");
            LOGGER.info(String.format("Received JSON message -> Timestamp = %s, Device ID = %d, Measurement Value = %f",
                    localDateTime, idDevice, measurementValue));
            logHourlyConsumption(hour, hourlyConsumption.get(hour));
        } catch (Exception e) {
            LOGGER.error("Error processing JSON message: " + e.getMessage());
        }
    }

    private int convertUUIDToInt(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return uuid.hashCode();
    }

    private void updateHourlyConsumption(int hour, Double measurementValue) {
        hourlyConsumption.put(hour, hourlyConsumption.getOrDefault(hour, 0.0) + measurementValue);
    }

    private void logHourlyConsumption(int hour, Double consumption) {
        LOGGER.info(String.format("Hour %d - Hourly Consumption: %f", hour+2, consumption));
    }
}
