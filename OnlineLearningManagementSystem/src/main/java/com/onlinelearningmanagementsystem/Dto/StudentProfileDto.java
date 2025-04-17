package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.Enum.EducationLevel;
import jakarta.persistence.Column;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentProfileDto {

    private String email;

    private String id;

    private String firstName;


    private String lastName;







    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private EducationLevel educationLevel;

    private String aboutMe;



    public StudentProfileDto(User user){
        this.email=user.getEmail();

        this.id= user.getAssignedId();

        this.firstName=user.getProfile().getFirstName();

        this.lastName=user.getProfile().getLastName();

        this.phoneNumber=user.getProfile().getPhoneNumber();

        this.dateOfBirth=user.getProfile().getDateOfBirth();



        this.educationLevel=user.getProfile().getStudentProfile().getEducationLevel();

        this.aboutMe=user.getProfile().getStudentProfile().getAboutMe();


    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public String getAboutMe() {
        return aboutMe;
    }
}
