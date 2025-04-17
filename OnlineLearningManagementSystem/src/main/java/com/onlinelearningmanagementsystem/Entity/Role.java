package com.onlinelearningmanagementsystem.Entity;

import com.onlinelearningmanagementsystem.Enum.RoleType;
import jakarta.persistence.*;

@Entity
@IdClass(RoleId.class)
@Table(name="role")
public class Role {

    @Id
    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name="user_id")
    private User user;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private RoleType role;

    public void setUser(User user) {
        this.user = user;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }



    @Override
    public String toString() {
        return "Role{" +
                "user='" + user + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
