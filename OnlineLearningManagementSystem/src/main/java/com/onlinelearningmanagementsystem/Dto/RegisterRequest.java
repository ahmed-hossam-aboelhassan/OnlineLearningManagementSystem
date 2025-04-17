package com.onlinelearningmanagementsystem.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class RegisterRequest {
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "firstname is required")
    private String firstName;

    @NotBlank(message = "lastname is required")
    private String lastName;

    @NotNull(message = "date of birth is required in this format yyyy-mm-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "phonenumber is required")
    private String phoneNumber;

    @NotNull(message = "roletype is required")
    private RoleType roleType;

    public String getEmail() {
        return email;
    }



    public String getPassword() {
        return password;
    }



    public String getFirstName() {
        return firstName;
    }



    public String getLastName() {
        return lastName;
    }



    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RoleType getRoleType() {
        return roleType;
    }
}
