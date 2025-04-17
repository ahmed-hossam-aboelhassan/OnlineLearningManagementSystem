package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.Tracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackerDao extends JpaRepository<Tracker,Integer> {
}
