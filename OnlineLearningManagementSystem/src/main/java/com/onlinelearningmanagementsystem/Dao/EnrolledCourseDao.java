package com.onlinelearningmanagementsystem.Dao;

import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Entity.EnrolledCourse;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnrolledCourseDao extends JpaRepository<EnrolledCourse, String > {
    @Query("from EnrolledCourse ec where ec.student.id=:id  and ec.enrollmentStatus=:es ")
    public Page<EnrolledCourse> getEnrolledCourseOfStudent(@Param("id")String  id,@Param("es") EnrollmentStatus enrollmentStatus, Pageable pageable);

    @Query("from EnrolledCourse ec where ec.course.id=:id  and ec.enrollmentStatus=:enrollmentStatus")
    public Page<EnrolledCourse> getStudentsOfCourse(@Param("id")Long  id,@Param("enrollmentStatus") EnrollmentStatus enrollmentStatus, Pageable pageable);

    @Query("from EnrolledCourse ec where ec.course.id=:id  and ec.enrollmentStatus=:enrollmentStatus")
    public List<EnrolledCourse> getListOfStudentsOfCourse(@Param("id")Long  id, @Param("enrollmentStatus") EnrollmentStatus enrollmentStatus);

    @Query("SELECT ec FROM EnrolledCourse ec WHERE ec.student.id = :studentId AND ec.course.id = :courseId")
    public EnrolledCourse findEnrollment(@Param("studentId") String studentId, @Param("courseId") Long courseId);

    @Query("SELECT ec FROM EnrolledCourse ec WHERE ec.student.id = :studentId AND ec.course.id = :courseId and ec.enrollmentStatus=:enrollmentStatus")
    public EnrolledCourse findEnrollment(@Param("studentId") String studentId, @Param("courseId") Long courseId,@Param("enrollmentStatus") EnrollmentStatus enrollmentStatus);

    @Transactional
    @Modifying
    @Query("delete  FROM EnrolledCourse ec WHERE ec.student.id = :studentId AND ec.course.id = :courseId")
    public void deleteEnrollment(@Param("studentId") String studentId, @Param("courseId") Long courseId);


}
