package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.SubmittedAssessment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StudentsSubmittedAssessmentDto {
    private String assessmentId;
    private String assessmentTitle;
    private BigDecimal grade;
    private LocalDateTime submittedAt;

    public StudentsSubmittedAssessmentDto(SubmittedAssessment submittedAssessment){
        this.assessmentId=submittedAssessment.getId();
        this.assessmentTitle=submittedAssessment.getAssessment().getTitle();
        this.grade=submittedAssessment.getGrade();
        this.submittedAt=submittedAssessment.getSubmissionDate();
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
