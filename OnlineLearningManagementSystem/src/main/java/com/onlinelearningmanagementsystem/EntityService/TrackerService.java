package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.TrackerDao;
import com.onlinelearningmanagementsystem.Entity.Tracker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackerService implements TrackerServiceInterface{

    @Autowired
    private TrackerDao trackerDao;

    @Transactional
    public void save(Tracker tracker){
        trackerDao.save(tracker);
    }

    public Tracker findTrackerbyId(int id){
        return trackerDao.findById(id).orElse(null);
    }
}
