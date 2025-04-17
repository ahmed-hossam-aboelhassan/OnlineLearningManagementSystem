package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.EnrolledCourseDao;
import com.onlinelearningmanagementsystem.Entity.EnrolledCourse;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrolledCourseService implements EnrolledCourseServiceInterface {
    @Autowired
    private EnrolledCourseDao enrolledCourseDao;

    @Transactional
    public void save(EnrolledCourse enrolledCourse){
        enrolledCourseDao.save(enrolledCourse);
    }

    @Transactional
    public void deleteEnrollment(String studentId,Long courseId){
        enrolledCourseDao.deleteEnrollment(studentId,courseId);
    }

    public Page<EnrolledCourse> getEnrolledCourseOfStudnet(String id,EnrollmentStatus enrollmentStatus, int page){
        Pageable pageable= PageRequest.of(page,2, Sort.by("enrollmentDate").descending());
        return enrolledCourseDao.getEnrolledCourseOfStudent(id,enrollmentStatus,pageable);
    }

    public Page<EnrolledCourse> getStudentsOfCourse(Long id, EnrollmentStatus enrollmentStatus, int page){
        Pageable pageable= PageRequest.of(page,2, Sort.by("enrollmentDate").descending());
        return enrolledCourseDao.getStudentsOfCourse(id,enrollmentStatus,pageable);
    }
    public EnrolledCourse findEnrollment(String studentId,Long courseId){
        return enrolledCourseDao.findEnrollment(studentId,courseId);
    }

    public EnrolledCourse findEnrollment(String studentId,Long courseId,EnrollmentStatus enrollmentStatus){
        return enrolledCourseDao.findEnrollment(studentId,courseId,enrollmentStatus);
    }
    public List<EnrolledCourse> getListOfStudentsOfCourse(Long  id,  EnrollmentStatus enrollmentStatus){
        return enrolledCourseDao.getListOfStudentsOfCourse(id,enrollmentStatus);
    }


}
