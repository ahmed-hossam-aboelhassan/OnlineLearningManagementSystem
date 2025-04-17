package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.EnrolledCourse;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface EnrolledCourseServiceInterface {
    @Transactional
     void save(EnrolledCourse enrolledCourse);


    @Transactional
    void deleteEnrollment(String studentId,Long courseId);

     Page<EnrolledCourse> getEnrolledCourseOfStudnet(String id,EnrollmentStatus enrollmentStatus, int page);

    Page<EnrolledCourse> getStudentsOfCourse(Long id, EnrollmentStatus enrollmentStatus, int page);

     EnrolledCourse findEnrollment(String studentId,Long courseId,EnrollmentStatus enrollmentStatus);
}
