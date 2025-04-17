package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, String> {
    @Query(" from User  where email= :reqemail")
    public User findUserByEmail(@Param("reqemail")String reqemail);

    @Query(" from User  where assignedId= :assignedId")
    public User findUserByAssignedId(@Param("assignedId")String assignedId);
}
