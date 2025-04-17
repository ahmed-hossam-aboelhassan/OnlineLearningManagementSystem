package com.onlinelearningmanagementsystem.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlinelearningmanagementsystem.Enum.AssessmentType;
import com.onlinelearningmanagementsystem.Enum.CriticalBonus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NewAssessmentDto {
    @NotNull(message = "assessment type is required")
    private AssessmentType assessmentType;

    @NotNull(message = "critical or bonus is required")
    private CriticalBonus criticalBonus;

    @NotNull(message = "weight is required in percentage and must be between 0 and 100")
    @Min(1)
    @Max(100)
    private BigDecimal weight;

    @NotNull(message = "there has to be a deadline")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deadLine;

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public  AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public CriticalBonus getCriticalBonus() {
        return criticalBonus;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
