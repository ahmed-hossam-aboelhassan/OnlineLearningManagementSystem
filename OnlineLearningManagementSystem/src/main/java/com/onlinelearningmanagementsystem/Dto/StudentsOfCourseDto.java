package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.EnrolledCourse;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;

import java.time.LocalDate;

public class StudentsOfCourseDto {
    private String id;
    private String fullName;
    private LocalDate enrollmentDate;
    private EnrollmentStatus enrollmentStatus;
    private String grade;

    public StudentsOfCourseDto(EnrolledCourse enrolledCourse){
        this.id=enrolledCourse.getStudent().getAssignedId();
        this.fullName=enrolledCourse.getStudent().getProfile().getFirstName()+" "+enrolledCourse.getStudent().getProfile().getLastName();
        this.enrollmentDate=enrolledCourse.getEnrollmentDate();
        this.enrollmentStatus=enrolledCourse.getEnrollmentStatus();
        this.grade= enrolledCourse.getGrade();
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public String getGrade() {
        return grade;
    }
}
