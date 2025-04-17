package com.onlinelearningmanagementsystem.Entity;

import com.onlinelearningmanagementsystem.Enum.AssessmentType;
import com.onlinelearningmanagementsystem.Enum.CriticalBonus;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessment")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "title",nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="assessment_type",nullable = false)
    private AssessmentType assessmentType;

    @Enumerated(EnumType.STRING)
    @Column(name="critical_bonus",nullable = false)
    private CriticalBonus criticalBonus;

    @Column(name = "weight",nullable = false)
    private BigDecimal weight;

    @Column(name = "established_at")
    private LocalDateTime establishedAt=LocalDateTime.now();

    @Column(name = "dead_line")

    private LocalDateTime deadLine;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "assessment",cascade = CascadeType.ALL)
    private List<SubmittedAssessment> submittedAssessments;

    public List<SubmittedAssessment> getSubmittedAssessments() {
        return submittedAssessments;
    }

    public void addSubmittedAssessment(SubmittedAssessment submittedAssessment){
        if(this.submittedAssessments==null)
            this.submittedAssessments=new ArrayList<>();

        this.submittedAssessments.add(submittedAssessment);
        submittedAssessment.setAssessment(this);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public CriticalBonus getCriticalBonus() {
        return criticalBonus;
    }

    public void setCriticalBonus(CriticalBonus criticalBonus) {
        this.criticalBonus = criticalBonus;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
    }

    public LocalDateTime getEstablishedAt() {
        return establishedAt;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }
}
