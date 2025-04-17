package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.UserDao;
import com.onlinelearningmanagementsystem.Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserDao userDao;



    public User findUserByAssignedId(String assignedId){
        return userDao.findUserByAssignedId(assignedId);
    }

    public User findUserById(String id){
        return userDao.findById(id).orElse(null);
    }
    public User findUserByEmail(String email){
        return userDao.findUserByEmail(email);
    }

    @Transactional
    public void saveUser(User user){
        userDao.save(user);
    }


}
