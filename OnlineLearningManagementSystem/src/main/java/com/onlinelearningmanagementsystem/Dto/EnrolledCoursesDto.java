package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.EnrolledCourse;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;

import java.time.LocalDate;

public class EnrolledCoursesDto {
    private String courseTitle;
    private String courseCode;
    private LocalDate enrollmentDate;
    private EnrollmentStatus enrollmentStatus;
    private String grade;


    public EnrolledCoursesDto(EnrolledCourse enrolledCourse){
        this.courseTitle=enrolledCourse.getCourse().getTitle();
        this.courseCode=enrolledCourse.getCourse().getCode();
        this.enrollmentDate=enrolledCourse.getEnrollmentDate();
        this.enrollmentStatus=enrolledCourse.getEnrollmentStatus();
        this.grade= enrolledCourse.getGrade();
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getCourseCode() {
        return courseCode;
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
