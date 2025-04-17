package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.StudentSubmittedAssessmentsDto;
import com.onlinelearningmanagementsystem.Dto.StudentsSubmittedAssessmentDto;
import com.onlinelearningmanagementsystem.Entity.Assessment;
import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Entity.SubmittedAssessment;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.EntityService.*;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SubmittedAssessmentQueryControllerService {
    @Autowired
    SubmittedAssessmentService submittedAssessmentService;
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
    private static final Logger logger = LoggerFactory.getLogger(SubmittedAssessmentQueryControllerService.class);
    private final String uploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/Submitted_Assessments/";
    @Autowired
    private PagedResourcesAssembler<StudentSubmittedAssessmentsDto> pagedResourcesAssemblerForStudentSubmittedAssessments;
    @Autowired
    private PagedResourcesAssembler<StudentsSubmittedAssessmentDto> pagedResourcesAssemblerForStudentsSubmittedAssessmentDto;


    private User extractUser(String token) {



        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private boolean checkIfCourseIsNull(Course course){
        return course==null;
    }

    private void checkIfCourseBelongsToThatInstructor(User instructor, Course course) {
        if(!course.getInstructor().equals(instructor)) {
            logger.error("Failed to get submissions as instructor {} not authorized ", instructor.getAssignedId());
            throw new UnauthorizedException("You aren't the instructor of this course");
        }
    }
    private void validateCourse(Course course,String courseCode){
        if(checkIfCourseIsNull(course)){
            logger.warn("Failed to update course {} as it doesn't exist",courseCode);
            throw new CourseNotFoundException("Wrong code, please type a valid code");
        }

    }
    private void courseActiveChecker(Course course){
        if(course.getCoursePhase()!= CoursePhase.ACTIVE){
            logger.warn("Failed to upload assessment for course {} as it isn't Active",course.getCode());
            throw new CourseNotActiveException("Course not active");

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
    private ResponseEntity<?> studentSubmissionsGetter(String courseCode,User user,int page){
        if(courseCode==null){
            logger.error("Failed to get submitted assessments as course code is null");
            throw new CourseNotFoundException("Please choose a valid course");
        }
        Course course=courseService.getCourseByCode(courseCode);
        validateCourse(course,courseCode);

        if(enrolledCourseService.findEnrollment(user.getId(),course.getId())==null){
            logger.error("Failed to get submissions as student {} not enrolled in course {}",user.getAssignedId(),course.getCode());
            throw new StudentNotEnrolledInCourseException("You aren't registered in this course");
        }
        Page<SubmittedAssessment> submittedAssessments=submittedAssessmentService.getSubmittedAssessmentsForStudent(course.getId(),user.getId(),page);
        if(submittedAssessments.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Page<StudentSubmittedAssessmentsDto>studentSubmittedAssessments=submittedAssessments.map(x->new StudentSubmittedAssessmentsDto(x));
        PagedModel<EntityModel<StudentSubmittedAssessmentsDto>> pagedModel=pagedResourcesAssemblerForStudentSubmittedAssessments.toModel(studentSubmittedAssessments);
        return ResponseEntity.ok(pagedModel);
    }
    private ResponseEntity<?> instructorSubmissionsGetter(String assessmentId,User user,int page){
        if(assessmentId==null){
            logger.error("Failed to get submitted assessments as assessment id is null");
            throw new AssessmentNotFoundException("Please choose a valid assessment");
        }
        Assessment assessment=assessmentService.getAssessmentById(assessmentId);
        assessmentNullChecker(assessment);

     checkIfCourseBelongsToThatInstructor(user,assessment.getCourse());
        Page<SubmittedAssessment> submittedAssessments=submittedAssessmentService.getSubmittedAssessmentsForInstructorByAssessmentId(assessmentId,page);
        if(submittedAssessments.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Page<StudentsSubmittedAssessmentDto>studentSubmittedAssessments=submittedAssessments.map(x->new StudentsSubmittedAssessmentDto(x));
        PagedModel<EntityModel<StudentsSubmittedAssessmentDto>> pagedModel=pagedResourcesAssemblerForStudentsSubmittedAssessmentDto.toModel(studentSubmittedAssessments);
        return ResponseEntity.ok(pagedModel);
    }

    public ResponseEntity<?> getSubmissions(String token,String courseCode,String assessmentId,int page){

        User user=extractUser(token);

        if(hasRole(user,RoleType.STUDENT)){
          return studentSubmissionsGetter(courseCode,user,page);
        }
        else{
           return instructorSubmissionsGetter(assessmentId,user,page);
        }

    }
    public ResponseEntity<?>getSubmittedAssessment(String token,String submittedAssessmentId){
        SubmittedAssessment submittedAssessment= submittedAssessmentService.getSubmittedAssessment(submittedAssessmentId);
        if(submittedAssessment==null){
            logger.error("Failed to get submitted assessments");
            throw new SubmittedAssessmentNotFoundException("Please choose a valid submitted assessment");
        }
        User instructor=extractUser(token);
        checkIfCourseBelongsToThatInstructor(instructor,submittedAssessment.getAssessment().getCourse());



        try {
            String temp=submittedAssessment.getAssessment().getTitle();
            Path filePath=Paths.get(uploadDir,submittedAssessment.getAssessment().getCourse().getCode(),temp);
            Path filePathAndName=filePath.resolve(submittedAssessment.getId()+".pdf");
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
                IOException e) { // Handle specific file-related errors
            logger.error("Error accessing assessment");
            throw new ErrorLoadingFileException ("Error retrieving File ");
        } catch (Exception e) {
            logger.error("Unexpected error while fetching assessment");
            throw new ErrorLoadingFileException ("Error retrieving File ");
        }
    }


}
