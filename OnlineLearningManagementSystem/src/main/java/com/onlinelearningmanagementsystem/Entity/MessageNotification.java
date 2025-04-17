package com.onlinelearningmanagementsystem.Entity;

import com.onlinelearningmanagementsystem.Enum.MessageOrNotification;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_notification")
public class MessageNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MessageOrNotification type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "sender_id", nullable = false, length = 50)
    private String senderId;

    @Column(name = "receiver_id", nullable = false, length = 50)
    private String receiverId;

    @Column(name = "timestamp", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp = LocalDateTime.now(); // Sets default in Java

    public void setType(MessageOrNotification type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public MessageOrNotification getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
