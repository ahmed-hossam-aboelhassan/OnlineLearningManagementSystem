package com.onlinelearningmanagementsystem.Entity;

import com.onlinelearningmanagementsystem.Enum.RoleType;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class RoleId implements Serializable {
    private String user; // Matches the User entity's ID type
    private RoleType role;

    public RoleId() {}

    public RoleId(String user, RoleType role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleId that = (RoleId) o;
        return Objects.equals(user, that.user) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
}