package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.MessageNotification;
import com.onlinelearningmanagementsystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface MessageNotificationDao extends JpaRepository<MessageNotification,String> {
    @Query(" from MessageNotification mn  where mn.receiverId= :userId")
    public List<MessageNotification> findUserMessagesAndNotifications(@Param("userId")String userId);
}
