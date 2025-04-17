package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.Tracker;
import jakarta.transaction.Transactional;

public interface TrackerServiceInterface {
    @Transactional
    void save(Tracker tracker);

    Tracker findTrackerbyId(int id);
}
