package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Enum.CourseLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class NewCourseRequest {

    private String instructorId;

    @NotBlank(message = "Course code is required")
    private String code;

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Course category is required")
    private String category;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    @NotBlank(message = "Course description is required")
    private String description;

    @NotNull(message = "Chapter count is required")
    @Min(value = 1, message = "Chapter count must be at least 1")
    private Integer chapter;


    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public int getChapter() {
        return chapter;
    }

    public String getInstructorId() {
        return instructorId;
    }
}
