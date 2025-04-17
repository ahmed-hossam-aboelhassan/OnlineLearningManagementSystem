package com.onlinelearningmanagementsystem.Entity;

import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "enrolled_course")
public class EnrolledCourse {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate=LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status")
    private EnrollmentStatus enrollmentStatus=EnrollmentStatus.ACTIVE;

    @Column(name = "course_grade")
    private String grade;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "course_id")
    private Course course;

    public String getId() {
        return id;
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

    public User getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }
}
