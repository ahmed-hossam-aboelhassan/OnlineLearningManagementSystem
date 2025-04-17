package com.onlinelearningmanagementsystem.Entity;

import com.onlinelearningmanagementsystem.Enum.EducationLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "instructor_profile")
public class InstructorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name="qualification")
    private String qualification;

    @Column(name = "teaching_experience")
    private int teachingExperience;

    public int getId() {
        return id;
    }

    public Profile getProfileId() {
        return profile;
    }

    public void setProfileId(Profile profile) {
        this.profile= profile;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getTeachingExperience() {
        return teachingExperience;
    }

    public void setTeachingExperience(int teachingExperience) {
        this.teachingExperience = teachingExperience;
    }
}
