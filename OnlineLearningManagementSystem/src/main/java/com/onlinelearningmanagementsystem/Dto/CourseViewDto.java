package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Enum.CourseLevel;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;



import java.time.LocalDate;

public class CourseViewDto {

    private String code;

    private String title;

    private String description;

    private String category;

    private CourseLevel level;

    private int chapter;

    private CoursePhase coursePhase;



    public CourseViewDto(Course course){
        this.chapter=course.getChapter();
        this.level=course.getLevel();
        this.category= course.getCategory();
        this.code=course.getCode();
        this.description=course.getDescription();
        this.title=course.getTitle();
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

    public CoursePhase getCoursePhase() {
        return coursePhase;
    }
}
