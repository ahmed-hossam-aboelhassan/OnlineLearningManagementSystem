package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Entity.*;
import com.onlinelearningmanagementsystem.EntityService.CourseService;
import com.onlinelearningmanagementsystem.EntityService.EnrolledCourseService;
import com.onlinelearningmanagementsystem.EntityService.SubmittedAssessmentService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EnrolledCourseCommandControllerService {
    @Autowired
    EnrolledCourseService enrolledCourseService;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    SubmittedAssessmentService submittedAssessmentService;

    private static final Logger logger = LoggerFactory.getLogger(EnrolledCourseCommandControllerService.class);

    private User extractUser(String token) {

        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private void checkIfCourseIsNull(Course course,String courseCode){
        if(course==null){
            logger.warn("Failed to un-enroll in course {} as it doesn't exist",courseCode);
            throw new CourseNotFoundException("Wrong code, please type a valid code");
        }

    }
    private void checkIfCourseIsInEnrollmentPhase(Course course){
        if(!course.getCoursePhase().equals(CoursePhase.ENROLLMENT)){
            logger.warn("Failed to enroll in course {} as it is {} ",course.getCode(),course.getCoursePhase());
            throw new CourseNotInEnrollmentPhaseException("Course is not in the enrollment phase.");
        }

    }
    private boolean hasRole(User user, RoleType roleType){
        boolean flag=false;
        for(Role role:user.getRoles()){
            if(role.getRole().equals(roleType)){
                flag=true;
                break;
            }
        }
        return flag;

    }

    private void createEnrollment(User student,Course course){
        EnrolledCourse enrolledCourse=new EnrolledCourse();
        enrolledCourse.setCourse(course);
        enrolledCourse.setStudent(student);
        enrolledCourseService.save(enrolledCourse);
    }

    private void deleteEnrollment(String studentId,Long courseId){
        enrolledCourseService.deleteEnrollment(studentId,courseId);

    }
    private void deleteSubmissions(User student, Course course){
        List<SubmittedAssessment>submissions=submittedAssessmentService.getSubmittedAssessmentsForStudentInList(course.getId(),student.getId());
        if(!submissions.isEmpty()){
                submissions.forEach(s->submittedAssessmentService.delete(s.getId()));
        }

    }
    private void checkGradeForReEnroll(EnrolledCourse enrolledCourse){
        BigDecimal grade=new BigDecimal(enrolledCourse.getGrade());
        if(grade.compareTo(BigDecimal.valueOf(61))==1){
            logger.warn("Failed to reEnroll in course {} as student{} got higher mark than 61",enrolledCourse.getCourse().getCode(),enrolledCourse.getStudent().getAssignedId());
            throw new GradeException("You cannot re-enroll because your grade exceeds 61");
        }

    }
    private ResponseEntity<?> adminCommand(String studentId,Course course,String type){
        if(studentId == null || studentId.isBlank()){
            logger.warn("Failed to enroll in course {} as studentId is empty",course.getCode());
            throw new StudentIdEmptyException("Student ID cannot be empty.");
        }

        User student=userService.findUserByAssignedId(studentId);
        if(student==null  || !hasRole(student,RoleType.STUDENT)){
            logger.warn("Failed to enroll student {} in course {} as studentId is invalid",studentId,course.getCode());
            throw new StudentIdInvalidException("Invalid Student ID format. It should start with '49-'.");
        }
        if(type.equals("ENROLLMENT"))
            return   processEnrollment(student,course);
        else if(type.equals("UNENROLLMENT"))
            return processUnEnrollment(student,course);
        else
            return processReEnrollment(student,course);
    }
    private void checkIfEnrolledIsNull(EnrolledCourse enrolledCourse,User student,Course course){
        if (enrolledCourse==null) {
            logger.warn("UnEnrollment failed: Student {} is not enrolled in course {}", student.getAssignedId(), course.getCode());
            throw new StudentNotEnrolledInCourseException("You are not enrolled in this course.");
        }
    }
    private ResponseEntity<?> processEnrollment(User student, Course course) {
        if (enrolledCourseService.findEnrollment(student.getId(), course.getId())!=null) {
            logger.warn("Enrollment failed: Student {} is already enrolled in course {}", student.getAssignedId(), course.getCode());
            throw new StudentAlreadyEnrolledInCourseException("You are already enrolled in this course.");
        }


        createEnrollment(student, course);
        logger.info("Successfully enrolled: Student {}  in course {}", student.getAssignedId(), course.getCode());
        return ResponseEntity.ok("Enrollment was successful.");
    }

    private ResponseEntity<?> processUnEnrollment(User student, Course course) {
        EnrolledCourse enrolledCourse=enrolledCourseService.findEnrollment(student.getId(), course.getId());
         checkIfEnrolledIsNull(enrolledCourse,student,course);
        if(enrolledCourse.getEnrollmentStatus().equals(EnrollmentStatus.COMPLETED)){
            logger.warn("UnEnrollment failed: Student {}  has already completed course {}", student.getAssignedId(), course.getCode());
            throw new StudentCompletedTheCourseException("You have already completed this course.");
        }




        deleteEnrollment(student.getId(), course.getId());
        deleteSubmissions(student,course);
        logger.info("Successfully Un-enrolled: Student {}  from course {}", student.getAssignedId(), course.getCode());
        return ResponseEntity.ok("Un-enrollment was successful.");
    }
    private ResponseEntity<?>processReEnrollment(User student ,Course course){
        EnrolledCourse enrolledCourse=enrolledCourseService.findEnrollment(student.getId(),course.getId(),EnrollmentStatus.COMPLETED);
        checkIfEnrolledIsNull(enrolledCourse,student,course);
        checkGradeForReEnroll(enrolledCourse);

        enrolledCourse.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        enrolledCourse.setGrade(null);
        enrolledCourse.getCourse().setEnrollmentCount(enrolledCourse.getCourse().getEnrollmentCount()+1);
        enrolledCourseService.save(enrolledCourse);
        logger.info("Successfully re enrolled student {} in course {}",student.getAssignedId(),course.getCode());
        return ResponseEntity.ok("Re enrollment successfull");

    }



    public ResponseEntity<?> enroll(String token,String courseCode,String studentId){
        Course course=courseService.getCourseByCode(courseCode);
        checkIfCourseIsNull(course,courseCode);
        checkIfCourseIsInEnrollmentPhase(course);
        User user=extractUser(token);
        if(hasRole(user,RoleType.ADMIN))
           return adminCommand(studentId,course,"ENROLLMENT");

       return processEnrollment(user,course);
    }


    public ResponseEntity<?>reEnroll(String token,String courseCode,String studentId){
        Course course=courseService.getCourseByCode(courseCode);
        checkIfCourseIsNull(course,courseCode);
        checkIfCourseIsInEnrollmentPhase(course);
        User user=extractUser(token);
        if(hasRole(user,RoleType.ADMIN))
            return adminCommand(studentId,course,"RE_ENROLLMENT");
        return processReEnrollment(user,course);


    }
    public ResponseEntity<?> unenroll(String token,String courseCode,String studentId){
        Course course=courseService.getCourseByCode(courseCode);
        checkIfCourseIsNull(course,courseCode);
        User user=extractUser(token);
        if(hasRole(user,RoleType.ADMIN))
            return adminCommand(studentId,course,"UNENROLLMENT");

        return processUnEnrollment(user,course);
    }




}
