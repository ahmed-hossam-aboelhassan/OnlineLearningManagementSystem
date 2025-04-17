package com.onlinelearningmanagementsystem.Entity;


import com.onlinelearningmanagementsystem.Enum.CourseLevel;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;

import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50,name = "code")
    private String code;

    @Column(nullable = false, length = 255,name = "title")
    private String title;

    @Column(columnDefinition = "TEXT",name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name="level")
    private CourseLevel level;

    @Column(name="chapter")
    private int chapter;

    @Column(name = "enrollment_count", nullable = false)
    private int enrollmentCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();



    @Enumerated(EnumType.STRING)
    @Column(name="course_status")
    private CoursePhase coursePhase;

    @ManyToOne(cascade ={CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH,CascadeType.PERSIST})
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<EnrolledCourse> students;

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Assessment>assessments;

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void addAssessment(Assessment assessment){
        if(assessments==null)
            assessments=new ArrayList<>();

        this.assessments.add(assessment);
        assessment.setCourse(this);
    }

    public List<EnrolledCourse> getStudents() {
        return students;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }



    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
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

    public String getCategory() {
        return category;
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

    public CourseLevel getLevel() {
        return level;
    }

    public void setLevel(CourseLevel level) {
        this.level = level;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public CoursePhase getCoursePhase() {
        return coursePhase;
    }

    public void setCoursePhase(CoursePhase coursePhase) {
        this.coursePhase = coursePhase;
    }
}

