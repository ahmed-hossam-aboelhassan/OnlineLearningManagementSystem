package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.Assessment;
import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Enum.CriticalBonus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentDao extends JpaRepository<Assessment,String> {
    @Query("from Assessment a where a.course.id=:id ")
    public List<Assessment> getAllAssessmentsOfCourse(@Param("id")Long id);

    @Query("from Assessment a where a.course.id=:id and a.criticalBonus=CRITICAL ")
    public List<Assessment> getAllCriticalAssessmentOfCourse(@Param("id")Long id);

    @Query("from Assessment a where a.course.id=:id order by a.establishedAt desc ")
    public Page<Assessment> getPageOfAllAssessmentsOfCourse(@Param("id")Long id, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete  FROM Assessment a WHERE a.id =:id")
    public  void deleteAssessmentById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("delete  FROM Assessment a WHERE a.course.id =:id")
    public  void deleteAssessmentByCourseId(@Param("id") Long id);
}

