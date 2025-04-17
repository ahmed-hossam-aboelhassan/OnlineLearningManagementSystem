package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.NewAssessmentDto;
import com.onlinelearningmanagementsystem.Entity.*;
import com.onlinelearningmanagementsystem.EntityService.*;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Enum.CriticalBonus;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import com.onlinelearningmanagementsystem.Mapper.AssessmentMapper;
import com.onlinelearningmanagementsystem.Mapper.CourseMapper;
import com.onlinelearningmanagementsystem.WebSocket.MyWebSocketHandler;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class AssessmentCommandControllerService {
    @Autowired
    AssessmentService assessmentService;
    @Autowired
    CourseService courseService;
    @Autowired
    EnrolledCourseService enrolledCourseService;

    @Autowired
    MessageNotificationService messageNotificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AssessmentMapper assessmentMapper;


    private static final Logger logger = LoggerFactory.getLogger(AssessmentCommandControllerService.class);
    private final String uploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/Assessments/";


    private User extractUser(String token) {



        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private boolean checkIfCourseIsNull(Course course){
        return course==null;
    }

    private boolean checkIfCourseBelongsToThatInstructor(User instructor, Course course) {
        return course.getInstructor() != null && course.getInstructor().equals(instructor);
    }
    private void validateCourse(Course course,String courseCode){
        if(checkIfCourseIsNull(course)){
            logger.warn("Failed to update course {} as it doesn't exist",courseCode);
            throw new CourseNotFoundException("Wrong code, please type a valid code");
        }
        courseActiveChecker(course);
    }
    private void validateFile(MultipartFile file,String courseCode){
        if (file.isEmpty()) {
            logger.warn("Failed to upload assessment file for {} as it is invalid or empty.", courseCode);
            throw new InvalidFileException("Please upload a valid file.");
        }


        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            logger.warn("Invalid file assessment type for {}. Only PDF files are allowed.", courseCode);
            throw new InvalidFileException("Only PDF files are allowed.");
        }
    }
    private void weightChecker(BigDecimal assessmentWeight,String assessmentTitle,Course course){
        if (assessmentWeight == null) {
            throw new InvalidWeightInputException("Weight cannot be null.");
        }
            if(assessmentWeight.compareTo(BigDecimal.valueOf(100))==1){
                logger.warn("Failed to upload assessment because the weight will be greater than 100");
                 throw new CourseWeightException("Please enter a weight less than or equal 100");
            }

        List<Assessment> assessments = assessmentService.getAllCriticalAssessmentOfCourse(course.getId());

        BigDecimal total = assessments.stream()
                .map(x->{
                    if(x.getTitle().equals(assessmentTitle))
                        return BigDecimal.ZERO;
                    else
                        return x.getWeight();

                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(assessmentWeight);

        if (total.compareTo(BigDecimal.valueOf(100)) > 0) {
            BigDecimal maxAllowed = BigDecimal.valueOf(100).subtract(total.subtract(assessmentWeight));
            throw new CourseWeightException(maxAllowed.compareTo(BigDecimal.ZERO) > 0
                    ? "Please enter a weight less than or equal " + maxAllowed
                    : "Maximum Weight Has Been Reached");
        }

        }
        private void pathChecker(String courseCode){
            Path uploadPath = Paths.get(uploadDir+courseCode);

            if (!Files.exists(uploadPath)) {
                try{ Files.createDirectories(uploadPath); }
                catch (Exception ex){
                    throw new FileStorageException("Error creating directory");
                }
            }
        }

        private void deadLineChecker(LocalDateTime deadLine){
        if(LocalDateTime.now().isAfter(deadLine)){
            throw new DeadLineException("Your deadline is already dead");
        }
        }

        private void updateHelper( Map<String,Object>updates,List<String> garbageValues,Assessment assessment){
            Map<String , Consumer<Object>>updateables=Map.of("criticalBonus",x->{ try{assessment.setCriticalBonus(CriticalBonus.valueOf((x.toString())));}catch (Exception ex){
            throw new IllegalArgumentException("CRITICAL or BONUS is expected");
            }
                    },
                    "deadLine",x->{try{
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        assessment.setDeadLine(LocalDateTime.parse(x.toString(),formatter));
                    }
                    catch(Exception ex){
                        throw new InvalidDateTimeFormatException("please enter a valid format like yyyy-MM-ddTHH:mm:ss");
                    }
                    },
                    "weight",x->{try{ assessment.setWeight(new BigDecimal(x.toString()));}
            catch(Exception ex){
                        throw new InvalidWeightInputException("please enter a number from 0-100");
                    }
                    }
            );

            updates.forEach((k,v)->{
                if(updateables.containsKey(k))
                    updateables.get(k).accept(v);
                else
                    garbageValues.add(k);
            });

            if(assessment.getCriticalBonus()==CriticalBonus.CRITICAL){
                weightChecker(assessment.getWeight(),assessment.getTitle(),assessment.getCourse());
            }
        }
    private void fileDeleter(Assessment assessment){
        Path uploadPath = Paths.get(uploadDir, assessment.getCourse().getCode());
        Path filePath = uploadPath.resolve(assessment.getTitle() + ".pdf");
        try{
            Files.delete(filePath);
        }
        catch (Exception ex){
            logger.error("Error uploading assessment for course {}", assessment.getCourse().getCode(),ex);
            throw new FileDeleteException("Error deleting assessment.");

        }
    }
    private void deleteQuery(Assessment assessment){
        try {
            assessmentService.deleteAssessmentById(assessment);
        }
        catch (Exception ex){
            logger.error("Error uploading assessment for course {}", assessment.getCourse().getCode(),ex);
            throw new FileDeleteException("Error deleting assessment.");
        }
    }
    private void courseActiveChecker(Course course){
        if(course.getCoursePhase()!=CoursePhase.ACTIVE){
            logger.warn("Failed to update course {} as it isn't Active",course.getCode());
            throw new CourseNotActiveException("Course not active");

        }
    }
    private void assessmentNullChecker(Assessment assessment){
        if(assessment==null){
            logger.warn("Failed to update assessment because id is wrong");
            throw new AssessmentNotFoundException("Assessment not found");
        }
    }
    private void checkIfAssessmentBelongsToInstructor(Assessment assessment,User instructor){
        if(assessment.getCourse() == null || !assessment.getCourse().getInstructor().equals(instructor)) {
            logger.warn("Failed to update assessment because it doesnt belong to that instructor");
            throw new UnauthorizedException("This assessment doesn't belong to that instructor");
        }
    }
    private void notificationSender(Course course,Assessment assessment,String type){
        MyWebSocketHandler wbh=new MyWebSocketHandler(userService,messageNotificationService);
        System.out.println(course.getStudents());
        List<EnrolledCourse>enrolledStudents=enrolledCourseService.getListOfStudentsOfCourse(course.getId(), EnrollmentStatus.ACTIVE);
        enrolledStudents.forEach(x-> wbh.sendMessageToUser("System",x.getStudent().getAssignedId(),assessment.getTitle()+" is now "+type+" for course "+course.getCode()+" with deadline "+assessment.getDeadLine().toString()));
    }






    @Transactional
    public ResponseEntity<?> uploadAssessment(String token, String courseCode, MultipartFile file, NewAssessmentDto newAssessmentDto){
        Course course=courseService.getCourseByCode(courseCode);

        validateCourse(course,courseCode);


        User user=extractUser(token);

        if(!checkIfCourseBelongsToThatInstructor(user,course)){
            logger.warn("Failed to upload course assessment as course {} doesn't belong to instructor {}",courseCode,user.getAssignedId());
            throw new UnauthorizedCourseUpdateException("This is not your course to upload assessment");
        }
        validateFile(file,courseCode);
        Assessment assessment= assessmentMapper.toAssessment(newAssessmentDto);
        assessment.setTitle("Temp assessment");

        if(assessment.getCriticalBonus().equals(CriticalBonus.CRITICAL))
            weightChecker(assessment.getWeight(),assessment.getTitle(),course);

        pathChecker(courseCode);

        int order=assessmentService.getAllAssessmentsOfCourse(course.getId()).size() +1;
        String filename ="Assessment_"+order;

        Path uploadPath = Paths.get(uploadDir, courseCode);
        Path filePath = uploadPath.resolve(filename + ".pdf");

        deadLineChecker(newAssessmentDto.getDeadLine());
        assessment.setTitle(filename);
        assessment.setCourse(course);
        assessment.setDeadLine(newAssessmentDto.getDeadLine());


        try {
            Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
            assessmentService.save(assessment);
            logger.info("Successfully uploaded a new assessment for {}",courseCode);
            notificationSender(assessment.getCourse(),assessment,"available");
            return ResponseEntity.status(HttpStatus.CREATED).body("Assessment uploaded successfully");

        } catch (Exception e) {

            logger.error("Error uploading assessment for course {}", courseCode,e);
            throw new FileUploadException("Error uploading assessment.", e);
        }





    }


    @Transactional
    public ResponseEntity<?> updateAssessment(String token, String assessmentId, Map<String,Object>updates){
        if (updates == null || updates.isEmpty()) {
            logger.warn("No updates provided");
            throw new AssessmentUpdatesException("No updates were provided");
        }
        List<String>garbageValues=new ArrayList<>();
        Assessment assessment=assessmentService.getAssessmentById(assessmentId);
        assessmentNullChecker(assessment);



        User instructor=extractUser(token);
        checkIfAssessmentBelongsToInstructor(assessment,instructor);
        courseActiveChecker(assessment.getCourse());

        updateHelper(updates, garbageValues,assessment);


        if(garbageValues.size()==updates.size()){
            logger.warn("Failed to update as Garbage values only were sent");
            throw new AssessmentUpdatesException("garbage values were sent");
        }
        assessmentService.save(assessment);
        notificationSender(assessment.getCourse(),assessment,"updated");
        logger.info("Successfully updated {} of course {}",assessment.getTitle(),assessment.getCourse().getCode());
        return ResponseEntity.ok("Successfully updated");

    }


    @Transactional
    public ResponseEntity<?> deleteAssessment(String token,String assessmentId){
        User instructor=extractUser(token);

        Assessment assessment=assessmentService.getAssessmentById(assessmentId);
        assessmentNullChecker(assessment);
        checkIfAssessmentBelongsToInstructor(assessment,instructor);
        courseActiveChecker(assessment.getCourse());
        fileDeleter(assessment);
        deleteQuery(assessment);

        logger.info("Successfully deleted {} of course {}",assessment.getTitle(),assessment.getCourse().getCode());
        return ResponseEntity.ok("Successfully deleted");


    }


}
