package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.EnrolledCoursesDto;
import com.onlinelearningmanagementsystem.Dto.StudentsOfCourseDto;
import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Entity.EnrolledCourse;
import com.onlinelearningmanagementsystem.Entity.Role;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.EntityService.CourseService;
import com.onlinelearningmanagementsystem.EntityService.EnrolledCourseService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.CourseNotFoundException;
import com.onlinelearningmanagementsystem.Exception.MissingParametersException;
import com.onlinelearningmanagementsystem.Exception.StudentIdInvalidException;
import com.onlinelearningmanagementsystem.Exception.UnauthorizedException;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EnrolledCourseQueryControllerService {
    @Autowired
    EnrolledCourseService enrolledCourseService;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private PagedResourcesAssembler<EnrolledCoursesDto> pagedResourcesAssemblerForEnrolledCoursesDto;

    @Autowired
    private PagedResourcesAssembler<StudentsOfCourseDto> pagedResourcesAssemblerForStudentsOfCourseDto;
    /*
    EntityModel<CourseDto> model = EntityModel.of(courseDto);
model.add(Link.of("http://localhost:8080/api/course/" + courseDto.getId(), "self"));
return ResponseEntity.ok(model);
     */
    /*
    List<EntityModel<CourseDto>> courses = courseList
        .stream()
        .map(course -> EntityModel.of(course)
                .add(Link.of("http://localhost:8080/api/course/" + course.getId(), "self")))
        .toList();

CollectionModel<EntityModel<CourseDto>> collectionModel = CollectionModel.of(courses);
     */

    private static final Logger logger = LoggerFactory.getLogger(EnrolledCourseQueryControllerService.class);

    private User extractUser(String token) {

        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private boolean checkIfCourseIsNull(Course course){
        return course==null;
    }
    private boolean checkIfCourseIsInEnrollmentPhase(Course course){
        return course.getCoursePhase().equals(CoursePhase.ENROLLMENT);
    }
    private boolean hasRole(User user, RoleType roleType){
        return user.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(roleType));

    }
    private boolean checkIfCourseIsDeleted(Course course){
        return course.getCoursePhase().equals(CoursePhase.DELETED);
    }
    private boolean checkIfCourseBelongsToThatInstructor(User instructor,Course course){
        return course.getInstructor().equals(instructor);

    }
    private ResponseEntity<PagedModel<?>> getEnrolledCoursesForStudent(User student,EnrollmentStatus enrollmentStatus,int page){
        Page<EnrolledCourse> enrolledCourses=enrolledCourseService.getEnrolledCourseOfStudnet(student.getId(),enrollmentStatus,page);
        if(enrolledCourses.isEmpty()) {
            logger.warn("No enrolled courses found for user {}", student.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Returns 204 No Content
        }
        Page<EnrolledCoursesDto> enrolledCoursesDto=enrolledCourses.map(EnrolledCoursesDto::new);
        PagedModel<EntityModel<EnrolledCoursesDto>> pagedModel = pagedResourcesAssemblerForEnrolledCoursesDto.toModel(enrolledCoursesDto);


        logger.info("Successfully fetched courses for student: {}",student.getAssignedId());
        return ResponseEntity.ok().body(pagedModel);
    }

    private ResponseEntity<PagedModel<?>> getStudentsOfCourse(String courseCode,EnrollmentStatus enrollmentStatus,int page,RoleType roleType,User instructor){
        if(courseCode!=null){
            Course course=courseService.getCourseByCode(courseCode);
            if(checkIfCourseIsNull(course)){
                logger.warn("Failed to load students of course: {} as course code is invalid", courseCode);
                throw new CourseNotFoundException("Wrong code, please type a valid code");
            }

            if(roleType.equals(RoleType.INSTRUCTOR) && !checkIfCourseBelongsToThatInstructor(instructor,course)){
                logger.warn("Failed to load students of course: {} as instructor is unauthorized", courseCode);
                throw new UnauthorizedException("Unauthorized instructor");
            }

            System.out.println(enrollmentStatus);
            Page<EnrolledCourse> enrolledCourses=enrolledCourseService.getStudentsOfCourse(course.getId(),enrollmentStatus,page);

            if(enrolledCourses.isEmpty()) {
                logger.warn("No enrolled students found for course {}", courseCode);
                return ResponseEntity.noContent().build(); // Returns 204 No Content
            }
            Page<StudentsOfCourseDto> studentsOfCourseDtos=enrolledCourses.map(StudentsOfCourseDto::new);
            PagedModel<EntityModel<StudentsOfCourseDto>> pagedModel = pagedResourcesAssemblerForStudentsOfCourseDto.toModel(studentsOfCourseDtos);

            logger.info("Successfully fetched students for course: {}",courseCode);
            return ResponseEntity.ok(pagedModel);

        }
        logger.warn("Failed to load students or course as parameters were missing");
        throw new MissingParametersException("Student id or course code should be passed");
    }

    public ResponseEntity<PagedModel<?>> fetchEnrollmentData(String token,String studentId, String courseCode, EnrollmentStatus enrollmentStatus,int page){
        User user=extractUser(token);
        if(hasRole(user,RoleType.STUDENT)){
           return getEnrolledCoursesForStudent(user,enrollmentStatus,page);

        }

        if(hasRole(user,RoleType.ADMIN)){
            if(studentId!=null){
                User student=userService.findUserByAssignedId(studentId);
                if(student==null || !hasRole(student,RoleType.STUDENT)){
                    logger.warn("Failed to load enrolled courses found for user {} as id is invalid", student.getId());
                    throw new StudentIdInvalidException("Invalid Student ID format. It should start with '49-'.");
                }
                return getEnrolledCoursesForStudent(student,enrollmentStatus,page);

            }
            return getStudentsOfCourse(courseCode,enrollmentStatus,page,RoleType.ADMIN,user);
        }

        return getStudentsOfCourse(courseCode,enrollmentStatus,page,RoleType.INSTRUCTOR,user);
    }
}
