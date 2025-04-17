package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Entity.SubmittedAssessment;
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
public interface SubmittedAssessmentDao extends JpaRepository<SubmittedAssessment,String> {
    @Query("SELECT sa FROM SubmittedAssessment sa WHERE sa.assessment.id=:assessment_id and sa.student.id=:student_id")
    public SubmittedAssessment getSubmittedAssessment(@Param("assessment_id") String assessmentId,@Param("student_id") String studentId);

    @Query(" FROM SubmittedAssessment sa WHERE  sa.student.id=:studentId and sa.assessment.course.id=:courseId")
    public Page<SubmittedAssessment> getSubmittedAssessmentsForStudent(@Param("courseId") Long courseId, @Param("studentId") String studentId, Pageable pageable);

    @Query(" FROM SubmittedAssessment sa WHERE  sa.student.id=:studentId and sa.assessment.course.id=:courseId")
    public List<SubmittedAssessment> getSubmittedAssessmentsForStudentInList(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    @Query(" FROM SubmittedAssessment sa WHERE  sa.assessment.id=:assessment_id")
    public Page<SubmittedAssessment> getSubmittedAssessmentsForInstructorByAssessmentId( @Param("assessment_id") String assessmentId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete  FROM SubmittedAssessment s WHERE s.assessment.id =:id")
    public  void deleteSubmissionByAssessmentId(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("delete  FROM SubmittedAssessment s WHERE s.id =:id")
    public  void deleteSubmissionById(@Param("id") String id);




}
