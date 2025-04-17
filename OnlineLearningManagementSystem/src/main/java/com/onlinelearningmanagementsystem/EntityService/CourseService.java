package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.CourseDao;
import com.onlinelearningmanagementsystem.Entity.Course;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CourseService implements CourseServiceInterface{

    @Autowired
    private CourseDao courseDao;

    public List<Course> getAllCourses(){
        return courseDao.findAll();
    }

    public Course getCourse(Long id){
        return courseDao.findById(id).orElse(null);
    }

    public Course getCourseByTitle(String title){
       return courseDao.getCourseByTitle(title);
    }


    public Course getCourseByCode(String code){
        return courseDao.getCourseByCode(code);

    }


    public List<Course> getCourseByInstructorId(String assignedId){
        return courseDao.getCourseByInstructorId(assignedId);


    }


    public List<Course> getCourseByCategory(String category){
        return courseDao.getCourseByCategory(category);

    }


   public List<Course> getCourseForInstructor(String id){
        return courseDao.getCourseForInstructor(id);

    }
    public Course checkIfCourseBelongsToInstructor(String code,String id){
        return courseDao.checkIfCourseBelongsToInstructor(code,id);
    }

    @Transactional
    public void save(Course course){
        courseDao.save(course);
    }

    @Transactional
    public void delete(Long id){
        courseDao.deleteCourseById(id);
    }
}
