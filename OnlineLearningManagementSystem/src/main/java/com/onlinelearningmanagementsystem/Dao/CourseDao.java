package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseDao extends JpaRepository<Course,Long> {
    @Query("from Course c where c.title=:title ")
    public Course getCourseByTitle(@Param("title")String title);

    @Query("from Course c where c.code=:code ")
    public Course getCourseByCode(@Param("code")String code);

    @Query("from Course c where c.code=:code and c.instructor.id=:id ")
    public Course checkIfCourseBelongsToInstructor(@Param("code")String code,@Param("id") String id);

    @Query("SELECT c FROM Course c WHERE c.instructor.assignedId = :assignedId ")
    public List<Course> getCourseByInstructorId(@Param("assignedId") String assignedId);

    @Query("SELECT c FROM Course c WHERE c.category= :category ")
    public List<Course> getCourseByCategory(@Param("category") String category);

    @Query("SELECT c FROM Course c WHERE c.instructor.id = :id")
    public  List<Course> getCourseForInstructor(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("delete  FROM Course c WHERE c.id =:id")
    public  void deleteCourseById(@Param("id") Long id);

}
