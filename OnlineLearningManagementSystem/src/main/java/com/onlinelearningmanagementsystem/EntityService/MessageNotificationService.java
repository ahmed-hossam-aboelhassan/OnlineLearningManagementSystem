package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.MessageNotificationDao;
import com.onlinelearningmanagementsystem.Entity.MessageNotification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageNotificationService implements MessageNotificationServiceInterface {
    @Autowired
    private MessageNotificationDao messageNotificationDao;

    public List<MessageNotification> findUserMessagesAndNotifications(String userId){
       return messageNotificationDao.findUserMessagesAndNotifications(userId);
    }

    @Transactional
    public void delete(MessageNotification messageNotification){
        messageNotificationDao.delete(messageNotification);
    }


    @Transactional
    public void save(MessageNotification messageNotification){
        messageNotificationDao.save(messageNotification);
    }


}
