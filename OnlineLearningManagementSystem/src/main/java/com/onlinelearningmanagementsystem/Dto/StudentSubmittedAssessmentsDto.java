package com.onlinelearningmanagementsystem.Dto;

import com.onlinelearningmanagementsystem.Entity.SubmittedAssessment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StudentSubmittedAssessmentsDto {
    private String assessmentTitle;
    private BigDecimal grade;
    private LocalDateTime submittedAt;

    public StudentSubmittedAssessmentsDto(SubmittedAssessment submittedAssessment){
        this.assessmentTitle=submittedAssessment.getAssessment().getTitle();
        this.grade=submittedAssessment.getGrade();
        this.submittedAt=submittedAssessment.getSubmissionDate();
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
