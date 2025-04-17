package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Enum.CourseLevel;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class AuthorizedCourseViewDto {
    private String code;

    private String title;

    private String description;

    private String category;

    private CourseLevel level;

    private int chapter;



    private int enrollmentCount ;



    private LocalDateTime createdAt = LocalDateTime.now();



    private LocalDateTime updatedAt = LocalDateTime.now();


    private CoursePhase coursePhase;



    public AuthorizedCourseViewDto(Course course){
        this.chapter=course.getChapter();
        this.level=course.getLevel();
        this.category= course.getCategory();
        this.code=course.getCode();
        this.description=course.getDescription();
        this.title=course.getTitle();
        this.enrollmentCount=course.getEnrollmentCount();
        this.createdAt=course.getCreatedAt();
        this.updatedAt=course.getUpdatedAt();
        this.coursePhase=course.getCoursePhase();


    }

    public String getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getChapter() {
        return chapter;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public int getEnrollmentCount() {
        return enrollmentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public CoursePhase getCoursePhase() {
        return coursePhase;
    }
}
