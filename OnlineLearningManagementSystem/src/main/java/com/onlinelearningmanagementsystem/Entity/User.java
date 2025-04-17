package com.onlinelearningmanagementsystem.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)  // Hibernate will generate UUID
    private String id;

    @Column(name = "email")
     private String email;

    @Column(name = "password")
     private String password;

    @Column(name="assigned_id")
    private String assignedId;

    @OneToOne( mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Role> roles;


    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private List<Course> instructorCourses;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<EnrolledCourse> studentCourses;

    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    private List<SubmittedAssessment> submittedAssessments;


    public List<SubmittedAssessment> getSubmittedAssessments() {
        return submittedAssessments;
    }

    public void addSubmittedAssessment(SubmittedAssessment submittedAssessment){
        if(this.submittedAssessments==null)
            this.submittedAssessments=new ArrayList<>();

        this.submittedAssessments.add(submittedAssessment);
        submittedAssessment.setStudent(this);
    }

    public List<EnrolledCourse> getStudentCourses() {
        return studentCourses;
    }

    public List<Course> getInstructorCourses() {
        return instructorCourses;
    }
    public void addCourseForInstructor(Course course){
        if(instructorCourses==null)
            instructorCourses=new ArrayList<>();

        instructorCourses.add(course);
        course.setInstructor(this);
    }

    public void setInstructorCourses(List<Course> instructorCourses) {
        this.instructorCourses = instructorCourses;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;

        profile.setUser(this);


    }

    public List<Role> getRoles() {
        return roles;
    }

  public void addRoles(Role role){
        if(roles==null)
            roles=new ArrayList<Role>();

        roles.add(role);
        role.setUser(this);
  }
}
