package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseServiceInterface {

   Course getCourseByTitle(String title);

   Course getCourseByCode(String code);

    List<Course> getCourseByInstructorId( String assignedId);

    List<Course> getCourseByCategory(String category);

    List<Course> getCourseForInstructor(String id);

    Course getCourse(Long id);
 Course checkIfCourseBelongsToInstructor(String code,String id);

    List<Course> getAllCourses();
    @Transactional
    void save(Course course);

    @Transactional
    void delete(Long id);
}
