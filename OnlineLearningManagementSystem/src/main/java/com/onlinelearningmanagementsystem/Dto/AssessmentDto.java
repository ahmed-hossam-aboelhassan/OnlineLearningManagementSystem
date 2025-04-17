package com.onlinelearningmanagementsystem.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlinelearningmanagementsystem.Entity.Assessment;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssessmentDto {
    private String id;
    private String assessmentTitle;
    private String courseCode;
    private BigDecimal weight;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime establishedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadLine;


    public AssessmentDto(Assessment assessment){
        this.id=assessment.getId();
        this.assessmentTitle=assessment.getTitle();
        this.courseCode=assessment.getCourse().getCode();
        this.weight=assessment.getWeight();
        this.establishedAt=assessment.getEstablishedAt();
        this.deadLine=assessment.getDeadLine();
    }

    public String getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public LocalDateTime getEstablishedAt() {
        return establishedAt;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }
}
