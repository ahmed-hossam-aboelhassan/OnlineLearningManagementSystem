package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.User;
import jakarta.transaction.Transactional;

public interface UserServiceInterface {
    @Transactional
    void saveUser(User user);

    User findUserByEmail(String email);

    User findUserById(String id);

    User findUserByAssignedId(String assignedId);
}
