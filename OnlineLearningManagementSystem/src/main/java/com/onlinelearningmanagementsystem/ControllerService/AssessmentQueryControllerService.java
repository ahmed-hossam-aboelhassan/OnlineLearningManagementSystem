package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.AssessmentDto;
import com.onlinelearningmanagementsystem.Dto.StudentsOfCourseDto;
import com.onlinelearningmanagementsystem.Entity.Assessment;
import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.EntityService.AssessmentService;
import com.onlinelearningmanagementsystem.EntityService.CourseService;
import com.onlinelearningmanagementsystem.EntityService.EnrolledCourseService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AssessmentQueryControllerService {
    @Autowired
    AssessmentService assessmentService;
    @Autowired
    CourseService courseService;
    @Autowired
    EnrolledCourseService enrolledCourseService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PagedResourcesAssembler<AssessmentDto> pagedResourcesAssemblerForAssessmentDto;

    private static final Logger logger = LoggerFactory.getLogger(AssessmentQueryControllerService.class);
    private final String uploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/Assessments/";
    private User extractUser(String token) {



        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private void checkIfCourseIsNull(Course course){
       if(course==null) {
           logger.warn("Failed to get course as course code is wrong");
           throw new CourseNotFoundException("Course code is wrong");
       }
    }

    private void checkIfStudentEnrolledInCourse(User student, Course course) {
         if(enrolledCourseService.findEnrollment(student.getId(),course.getId(), EnrollmentStatus.ACTIVE)==null){
             logger.warn("Student {} not enrolled in course{}",student.getAssignedId(),course.getCode());
             throw new UnauthorizedException("You are not enrolled in this course to view assessments");
         }
    }

    private void checkIfCourseBelongsToThatInstructor(User instructor, Course course) {
        if(course.getInstructor() == null || !course.getInstructor().equals(instructor)){
            logger.warn("Failed to view course assessments as course {} doesn't belong to instructor {}",course.getCode(),instructor.getAssignedId());
            throw new UnauthorizedCourseUpdateException("This is not your course to view assessments");
        }
    }

    private void assessmentNullChecker(Assessment assessment){
        if(assessment==null){
            logger.warn("Failed to update assessment because id is wrong");
            throw new AssessmentNotFoundException("Assessment not found");
        }
    }
    private boolean hasRole(User user, RoleType roleType){
        return user.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(roleType));

    }



    public ResponseEntity<PagedModel<?>> getAssessmentsByCourseCode(String token,String courseCode,int page){
        Course course=courseService.getCourseByCode(courseCode);
        checkIfCourseIsNull(course);

        User user=extractUser(token);
        if(hasRole(user,RoleType.STUDENT))
            checkIfStudentEnrolledInCourse(user,course);
        else if(!hasRole(user,RoleType.ADMIN)){
            checkIfCourseBelongsToThatInstructor(user,course);
        }

        Page<Assessment> assessments=assessmentService.getPageOfAllAssessmentsOfCourse(course.getId(),page);
        if(assessments.isEmpty()){
            logger.info("No content found");
            return ResponseEntity.noContent().build();
        }
        Page<AssessmentDto>assessmentDtos=assessments.map(x->new AssessmentDto(x));
        PagedModel<EntityModel<AssessmentDto>>pagedModel=pagedResourcesAssemblerForAssessmentDto.toModel(assessmentDtos);

        return ResponseEntity.ok().body(pagedModel);

    }

    public ResponseEntity<?> getAssessment(String token,String assessmentId) {

        Assessment assessment=assessmentService.getAssessmentById(assessmentId);
        assessmentNullChecker(assessment);
        checkIfCourseIsNull(assessment.getCourse());
        User user=extractUser(token);
        if(hasRole(user,RoleType.STUDENT))
            checkIfStudentEnrolledInCourse(user,assessment.getCourse());
        else if(!hasRole(user,RoleType.ADMIN)){
            checkIfCourseBelongsToThatInstructor(user,assessment.getCourse());
        }




        try {

            Path filePath = Paths.get(uploadDir,assessment.getCourse().getCode() );
            Path filePathAndName=filePath.resolve(assessment.getTitle()+".pdf");
            Resource resource = new UrlResource(filePathAndName.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.out.println(resource);
                logger.error("Error cannot get to assessment");
                throw new FileNotFoundException("Assessment not found");

            }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }  catch (
    IOException e) {
        logger.error("Error accessing assessment");
        throw new ErrorLoadingFileException ("Error retrieving File ");
    } catch (Exception e) {
        logger.error("Unexpected error while fetching assessment");
        throw new ErrorLoadingFileException ("Error retrieving File ");
    }
    }


}
