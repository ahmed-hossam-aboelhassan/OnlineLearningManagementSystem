package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.MessageNotification;
import jakarta.transaction.Transactional;

import java.util.List;

public interface MessageNotificationServiceInterface {
     List<MessageNotification> findUserMessagesAndNotifications(String userId);

    @Transactional
   void delete(MessageNotification messageNotification);


    @Transactional
    void save(MessageNotification messageNotification);
}
