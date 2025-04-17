package com.onlinelearningmanagementsystem.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/message") // Clients send messages here
    @SendTo("/topic/messages")  // Broadcasts to all subscribers
    public String sendMessage(String message) {
        return "Server: " + message;
    }
}
