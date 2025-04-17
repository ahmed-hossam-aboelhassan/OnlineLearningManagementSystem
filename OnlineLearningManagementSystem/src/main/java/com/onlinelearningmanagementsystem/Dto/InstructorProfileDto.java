package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.InstructorProfile;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.Enum.EducationLevel;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class InstructorProfileDto {
    private String email;

    private String id;

    private String firstName;


    private String lastName;






    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String qualification;

    private int teachingExperience;



    public InstructorProfileDto(User user){
        this.email=user.getEmail();
        this.id= user.getAssignedId();
        this.firstName=user.getProfile().getFirstName();
        this.lastName=user.getProfile().getLastName();

        this.phoneNumber=user.getProfile().getPhoneNumber();
        this.dateOfBirth=user.getProfile().getDateOfBirth();


        this.qualification=user.getProfile().getInstructorProfile().getQualification();
        this.teachingExperience=user.getProfile().getInstructorProfile().getTeachingExperience();


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

    public String getQualification() {
        return qualification;
    }

    public int getTeachingExperience() {
        return teachingExperience;
    }

}
