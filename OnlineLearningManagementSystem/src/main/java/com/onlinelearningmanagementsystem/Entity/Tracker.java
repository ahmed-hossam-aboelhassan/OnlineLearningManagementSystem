package com.onlinelearningmanagementsystem.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tracker")
public class Tracker {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "lateststudent")
    private int lateststudent;

    @Column(name = "latestinstructor")
    private int latestinstructor;

    @Column(name = "latestadmin")
    private int latestadmin;

    public int getId() {
        return id;
    }

    public int getLateststudent() {
        return lateststudent;
    }

    public void setLateststudent(int lateststudent) {
        this.lateststudent = lateststudent;
    }

    public int getLatestinstructor() {
        return latestinstructor;
    }

    public void setLatestinstructor(int latestinstructor) {
        this.latestinstructor = latestinstructor;
    }

    public int getLatestadmin() {
        return latestadmin;
    }

    public void setLatestadmin(int latestadmin) {
        this.latestadmin = latestadmin;
    }
}
