package com.onlinelearningmanagementsystem.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinelearningmanagementsystem.Entity.MessageNotification;
import com.onlinelearningmanagementsystem.EntityService.MessageNotificationService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.MessageOrNotification;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private final UserService userService;
    private final MessageNotificationService messageNotificationService;

    public MyWebSocketHandler( UserService userService,MessageNotificationService messageNotificationService) {
        this.userService = userService;
        this.messageNotificationService=messageNotificationService;
    }
    // Store WebSocket sessions by username
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Get the session name from the session attributes
        String sessionName = (String) session.getAttributes().get("userId");
        sessions.put(sessionName, session);
        loadOfflineMessagesAndNotifications(session);

        }

    private void loadOfflineMessagesAndNotifications(WebSocketSession session){
        List<MessageNotification> messageNotificationList=messageNotificationService.findUserMessagesAndNotifications((String)session.getAttributes().get("userId"));
        if(messageNotificationList!=null && !messageNotificationList.isEmpty()){
            messageNotificationList.forEach(m-> {
                try {

                    session.sendMessage(new TextMessage(m.getType()+": "+m.getMessage()+", From: "+m.getSenderId()+"  sent at:"+m.getTimestamp()));
                    messageNotificationService.delete(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            });
        }
    }

    private void offlineMessageSender(String sender, String userId, String message){
        MessageNotification messageNotification = new MessageNotification();
        messageNotification.setMessage(message);
        messageNotification.setReceiverId(userId);
        messageNotification.setSenderId(sender);
        messageNotification.setType(MessageOrNotification.MESSAGE);
        messageNotificationService.save(messageNotification);
        System.out.println("Message saved successfully ");

        WebSocketSession thisSession = sessions.get(sender);
        if (thisSession != null) {
            try {
                thisSession.sendMessage(new TextMessage("Message sent but user not online"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public boolean userExists(String id){
        return  userService.findUserByAssignedId(id)!=null;
        }



    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receivedMessage = message.getPayload();
        System.out.println("Received: " + receivedMessage);

        // Parse the incoming JSON message
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(receivedMessage);

        String action = jsonNode.get("action").asText();

        if ("sendMessage".equals(action)) {
            String targetUser = jsonNode.get("targetUser").asText();
            String messageToSend = jsonNode.get("message").asText();


            if(userExists(targetUser))
                sendMessageToUser((String)session.getAttributes().get("userId"),targetUser, messageToSend);
            else
                session.sendMessage(new TextMessage("Unknown user: " + targetUser));
        } else {
            // Handle other actions
            session.sendMessage(new TextMessage("Unknown action: " + action));
        }
    }


    public void sendMessageToUser(String sender,String userId, String message) {
        WebSocketSession targetSession = sessions.get(userId);

        if (targetSession != null && targetSession.isOpen()) {
            // Send message if user is online
            try {
                targetSession.sendMessage(new TextMessage(message +" from:"+sender+" at:"+ LocalDateTime.now().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            offlineMessageSender( sender,userId, message);

        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // Get the session name from the session attributes
        String sessionName = (String) session.getAttributes().get("userId");
        if (sessionName != null) {
            sessions.remove(sessionName);  // Remove session from map when the user disconnects

        }
    }

}
