package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Entity.*;
import com.onlinelearningmanagementsystem.EntityService.AssessmentService;
import com.onlinelearningmanagementsystem.EntityService.CourseService;
import com.onlinelearningmanagementsystem.EntityService.SubmittedAssessmentService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmittedAssessmentCommandControllerService {
    @Autowired
    SubmittedAssessmentService submittedAssessmentService;
    @Autowired
    AssessmentService assessmentService;
    @Autowired
    CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(SubmittedAssessmentCommandControllerService.class);
    private final String uploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/Submitted_Assessments/";


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
        courseActiveChecker(course);
    }
    private void validateFile(MultipartFile file){
        if (file.isEmpty()) {
            logger.warn("Failed to upload assessment file as it is invalid or empty.");
            throw new InvalidFileException("Please upload a valid file.");
        }


        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            logger.warn("Invalid file assessment type . Only PDF files are allowed.");
            throw new InvalidFileException("Only PDF files are allowed.");
        }
    }
    private void pathChecker(Path uploadPath){


        if (!Files.exists(uploadPath)) {
            try{ Files.createDirectories(uploadPath); }
            catch (Exception ex){
                throw new FileStorageException("Error creating directory");
            }
        }
    }

    private void deadLineChecker(LocalDateTime deadLine){
        if(LocalDateTime.now().isAfter(deadLine)){
            throw new DeadLineException("Deadline has already passed");
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
    private void studentRegisteredInAssessmentCourse(List<EnrolledCourse> enrolledCourses, Course course){
        if(enrolledCourses.stream().map(x->x.getCourse()).noneMatch(x->x.equals(course))){
            logger.error("Failed to submit because student isn't enrolled in this course");
            throw new StudentNotEnrolledInCourseException("You aren't in this course to submit an assessment");
        }
    }
    private ResponseEntity<?> fileAndEntitySaver(MultipartFile file,Path filePath,SubmittedAssessment submittedAssessment,String option){
        try {
            Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
            submittedAssessmentService.save(submittedAssessment);
            logger.info("Successfully {} {} for {}",option,submittedAssessment.getAssessment().getTitle(),submittedAssessment.getAssessment().getCourse().getCode());
            return ResponseEntity.ok().body("Assessment "+option+ " successfully");

        } catch (Exception e) {

            logger.error("Error {} assessment ",option);
            throw new FileUploadException("Error "+option+" assessment.", e);
        }

    }
    private ResponseEntity<?> reSubmitter(SubmittedAssessment submittedAssessment, MultipartFile file){
        courseActiveChecker(submittedAssessment.getAssessment().getCourse());
        deadLineChecker(submittedAssessment.getAssessment().getDeadLine());
        validateFile(file);
        Path uploadPath=Paths.get(uploadDir,submittedAssessment.getAssessment().getCourse().getCode(),submittedAssessment.getAssessment().getTitle());
        pathChecker(uploadPath);
        Path filePath=uploadPath.resolve(submittedAssessment.getId());
        submittedAssessment.setSubmissionDate(LocalDateTime.now());

        return fileAndEntitySaver(file,filePath,submittedAssessment,"resubmit");


    }
    private SubmittedAssessment reSubmissionChecker(String assessmentId,String studentId,String submitOrResubmit){
        SubmittedAssessment submittedAssessment= submittedAssessmentService.getSubmittedAssessment(assessmentId,studentId);

        if(submittedAssessment!=null && submitOrResubmit.equals("SUBMIT")){
            logger.error("Failed to submit because student already submitted once");
            throw new AssessmentResubmissionException("You already submitted once try to resubmit");
        }
        else if(submittedAssessment==null && submitOrResubmit.equals("RESUBMIT")){
            logger.error("Failed to resubmit because student didn't submit once");
            throw new AssessmentResubmissionException("You didn't submit this assessment to resubmit");
        }
        return submittedAssessment;

    }
    private SubmittedAssessment newSubmissionCreater(Assessment assessment,User student){
        SubmittedAssessment submittedAssessment=new SubmittedAssessment();
        submittedAssessment.setAssessment(assessment);
        submittedAssessment.setStudent(student);
        submittedAssessment.setSubmissionDate(LocalDateTime.now());
        submittedAssessmentService.save(submittedAssessment);

        return submittedAssessment;
    }
    private void gradeChecker(SubmittedAssessment submittedAssessment,BigDecimal grade){
        if(grade.compareTo(submittedAssessment.getAssessment().getWeight())==1){
            logger.error("Failed to grade because the grade is greater than weight");
            throw new GradeException("Grade must be maximum {"+submittedAssessment.getAssessment().getWeight()+"}");
        }
    }

    public ResponseEntity<?> submitAssessment(String token,MultipartFile file,String assessmentId){
        Assessment assessment=assessmentService.getAssessmentById(assessmentId);
        assessmentNullChecker(assessment);
        User student=extractUser(token);
        reSubmissionChecker(assessmentId,student.getId(),"SUBMIT");
        studentRegisteredInAssessmentCourse(student.getStudentCourses(),assessment.getCourse());
        courseActiveChecker(assessment.getCourse());
        deadLineChecker(assessment.getDeadLine());
        validateFile(file);
        Path uploadPath=Paths.get(uploadDir,assessment.getCourse().getCode(),assessment.getTitle());
        System.out.println(assessment.getTitle());
        pathChecker(uploadPath);
        SubmittedAssessment submittedAssessment=newSubmissionCreater(assessment,student);
        Path filePath=uploadPath.resolve(submittedAssessment.getId()+".pdf");
        System.out.println(filePath);


        return fileAndEntitySaver(file,filePath,submittedAssessment,"submit");
    }

    public ResponseEntity<?> reSubmit(String token,String assessmentId,MultipartFile file){
        Assessment assessment=assessmentService.getAssessmentById(assessmentId);
        assessmentNullChecker(assessment);
        User student=extractUser(token);
        SubmittedAssessment submittedAssessment=reSubmissionChecker(assessmentId,student.getId(),"RESUBMIT");
        return reSubmitter(submittedAssessment,file);

    }

   public ResponseEntity<?> updateGrade(String token, String submittedAssessmentId, BigDecimal grade){
        User instructor=extractUser(token);
        SubmittedAssessment submittedAssessment=submittedAssessmentService.getSubmittedAssessment(submittedAssessmentId);
        if(submittedAssessment==null){
            logger.error("Failed to get submitted assessments");
            throw new SubmittedAssessmentNotFoundException("Please choose a valid submitted assessment");
        }
        checkIfCourseBelongsToThatInstructor(instructor,submittedAssessment.getAssessment().getCourse());
        courseActiveChecker(submittedAssessment.getAssessment().getCourse());
        gradeChecker(submittedAssessment,grade);
        submittedAssessment.setGrade(grade);
        submittedAssessmentService.save(submittedAssessment);
        logger.info("Successfully updated submitted assessment grade by {}",instructor.getAssignedId());
        return ResponseEntity.ok("Grade updated successfully");
   }

}
