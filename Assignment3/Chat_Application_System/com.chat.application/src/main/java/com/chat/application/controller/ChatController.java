package com.chat.application.controller;

import com.chat.application.model.Message;
import com.chat.application.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@EnableWebSocketMessageBroker
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final ConcurrentHashMap<String, List<Message>> userMessages = new ConcurrentHashMap<>();



    @MessageMapping("/message")   // /app/message
    @SendTo("/chatroom/public")
    private Message receivePublicMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);  // /user/Oana/private

        storeMessage(message.getSenderName(), message.getReceiverName(), message);

        return message;
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public Message handleTypingEvent(@Payload Message typingNotification) {
        return typingNotification;
    }

    @MessageMapping("/seen")
    public void handleSeenNotification(@Payload Message seenNotification) {
        updateMessageStatus(seenNotification.getSenderName(), seenNotification.getReceiverName(), Status.SEEN);

        broadcastMessages(seenNotification.getSenderName(), seenNotification.getReceiverName());
    }

    private void storeMessage(String senderName, String receiverName, Message message) {
        // For simplicity, creating a new list for each pair of users
        userMessages.computeIfAbsent(generateKey(senderName, receiverName), k -> new CopyOnWriteArrayList<>()).add(message);
    }

    private void updateMessageStatus(String senderName, String receiverName, Status newStatus) {
        List<Message> messages = userMessages.get(generateKey(senderName, receiverName));
        if (messages != null) {
            messages.forEach(message -> {
                if (message.getStatus() == Status.MESSAGE) {
                    message.setStatus(newStatus);
                }
            });
        }
    }

    private void broadcastMessages(String senderName, String receiverName) {
        simpMessagingTemplate.convertAndSendToUser(senderName, "/private", userMessages.get(generateKey(senderName, receiverName)));
        simpMessagingTemplate.convertAndSendToUser(receiverName, "/private", userMessages.get(generateKey(senderName, receiverName)));
    }

    private String generateKey(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "-" + user2 : user2 + "-" + user1;
    }
}
