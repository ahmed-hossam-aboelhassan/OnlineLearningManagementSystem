package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.NewCourseRequest;
import com.onlinelearningmanagementsystem.Entity.*;
import com.onlinelearningmanagementsystem.EntityService.*;
import com.onlinelearningmanagementsystem.Enum.*;
import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import com.onlinelearningmanagementsystem.Mapper.CourseMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class CourseCommandControllerService {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    EnrolledCourseService enrolledCourseService;

    @Autowired
    SubmittedAssessmentService submittedAssessmentService;

    @Autowired
    AssessmentService assessmentService;

    private final String assessmentUploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/Assessments/";
    private final String submittedAssessmentUploadDir = "C:/Users/Administrator/Desktop/Projects/OnlineLearningManagementSystemProject/Submitted_Assessments/";

    private static final Logger logger = LoggerFactory.getLogger(CourseCommandControllerService.class);

    private User extractUser(String token) {



        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private void checkIfCourseIsNull(Course course){
        if(course==null){
            logger.warn("Failed to update course {} as it doesn't exist",course.getCode());
            throw new CourseNotFoundException("Wrong code, please type a valid code");
        }

    }

    private boolean checkIfCourseCodeIsTaken(String code){
        return courseService.getCourseByCode(code)!=null;
    }
    private boolean checkIfCourseTitleIsTaken(String title){
        return courseService.getCourseByTitle(title)!=null;
    }
    private boolean checkIfCourseBelongsToThatInstructor(User instructor, Course course) {
        return course.getInstructor() != null && course.getInstructor().equals(instructor);
    }




    private boolean hasRole(User user, RoleType roleType){
        return user.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(roleType));

    }
    private boolean checkIfCourseIsDeleted(Course course){
        return course.getCoursePhase().equals(CoursePhase.DELETED);
    }
    private void updateCourseHelper(Course course,String key,String value){


        Map<String, Consumer<String>>helper=Map.of(
                "code",c->course.setCode(c),
                "title",c->course.setTitle(c),
                "description",c->course.setDescription(c),
                "category",c->course.setCategory(c),
                "chapter",c->course.setChapter(Integer.parseInt(c)),
                "level",c->{
                    if(c.equals("ADVANCED"))
                        course.setLevel(CourseLevel.ADVANCED);
                    else if(c.equals("INTERMEDIATE"))
                        course.setLevel(CourseLevel.INTERMEDIATE);

                    else
                        course.setLevel(CourseLevel.BEGINNER);
                });
        if(helper.containsKey(key))
            helper.get(key).accept(value);

    }
    private void addsUp(List<Assessment> assessments){
        BigDecimal acc=assessments.stream().filter(a->a.getCriticalBonus()==CriticalBonus.CRITICAL).map(a->a.getWeight()).reduce(BigDecimal.ZERO,(c, a)->c.add(a));
        if(!acc.equals(BigDecimal.valueOf(100))){
            logger.warn("Failed to finish course as assessments weight doesn't add up to 100");
            throw new CourseWeightException("Update assessment weight to add up to 100 to finish course");
        }
    }
    private void gradeStudents(EnrolledCourse enrolledCourse,List<Assessment> assessments){
        BigDecimal acc=BigDecimal.ZERO;
        for(Assessment assessment :assessments){
            SubmittedAssessment submittedAssessment=submittedAssessmentService.getSubmittedAssessment(assessment.getId(),enrolledCourse.getStudent().getId());
            if(submittedAssessment!=null){
                acc=acc.add(submittedAssessment.getGrade());
            }
        }
        enrolledCourse.setGrade(acc.toString());
        enrolledCourse.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
        enrolledCourseService.save(enrolledCourse);
    }
    public void deleteCourseFiles(Course course) {
        deleteFolder(Paths.get(assessmentUploadDir, course.getCode()), "assessments",course);
        deleteFolder(Paths.get(submittedAssessmentUploadDir, course.getCode()), "submitted assessments",course);
    }

    private void deleteFolder(Path folderPath, String folderType,Course course) {
        try {
            Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // Delete files first
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // Delete folder after files are deleted
                    return FileVisitResult.CONTINUE;
                }
            });
            logger.info("{} folder deleted successfully: {}", folderType, folderPath);
        } catch (IOException ex) {
            logger.error("Error deleting {} for course {}", folderType, course.getCode(), ex);
            throw new FileDeleteException("Error deleting "+folderType+" for course "+course.getCode());
        }
    }


    public void deleteFromDb(Course course){

        course.getAssessments().forEach(a->submittedAssessmentService.deleteByAssessmentId(a.getId()));
        assessmentService.deleteAssessmentsByCourseId(course.getId());

    }
    @Transactional
    public ResponseEntity<?> createCourse(String token, NewCourseRequest newCourseRequest) {

        User user = extractUser(token);


        if (checkIfCourseCodeIsTaken(newCourseRequest.getCode())) {
            logger.warn("Failed to create course as code {} is already taken", newCourseRequest.getCode());
            throw new CourseCodeAlreadyTakenException("This course code is already taken, choose another one.");
        }

        if (checkIfCourseTitleIsTaken(newCourseRequest.getTitle())) {
            logger.warn("Failed to create course as title: {} is already taken", newCourseRequest.getTitle());
            throw new CourseTitleAlreadyTakenException("This course title is already taken, choose another one.");
        }


        Course course = courseMapper.toCourse(newCourseRequest);
        if (hasRole(user, RoleType.ADMIN)) {
            if (newCourseRequest.getInstructorId() == null) {
                if (hasRole(user, RoleType.INSTRUCTOR)) {
                    course.setInstructor(user);
                } else {
                    logger.warn("Failed to create course as instructor id is empty");
                    throw new EmptyInstructorIdException("Instructor ID cannot be empty.");
                }
            } else if (newCourseRequest.getInstructorId().startsWith("INS-")) {

                User temp = userService.findUserByAssignedId(newCourseRequest.getInstructorId());

                if (temp == null) {
                    logger.warn("Failed to create course as instructor id in invalid");
                    throw new InvalidInstructorIdException("Please enter a valid instructor ID.");
                }

                course.setInstructor(temp);
            }
            else{
                logger.warn("Failed to create course as instructor id in invalid");
                throw new InvalidInstructorIdException("Please enter a valid instructor ID.");
            }

        }
            else
                course.setInstructor(user);

            course.setCoursePhase(CoursePhase.IN_ACTIVE);
            courseService.save(course);


            logger.info("Successfully created course {} for instructor {}", course.getCode(), course.getInstructor().getAssignedId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully");



    }

    @Transactional
    public ResponseEntity<?> updateCourse(String token, String code, Map<String,String> courseAttributes, CoursePhase coursePhase){

        User user=extractUser(token);


        Course course= courseService.getCourseByCode(code);
   checkIfCourseIsNull(course);
        if(checkIfCourseIsDeleted(course)){
            if(hasRole(user,RoleType.ADMIN) &&coursePhase!=null &&coursePhase.equals(CoursePhase.IN_ACTIVE)){
                course.setCoursePhase(CoursePhase.IN_ACTIVE);
                course.setUpdatedAt(LocalDateTime.now());
                courseService.save(course);
                logger.info("Successfully restored course {} as course was deleted", code);
                return ResponseEntity.ok("Course is restored");
            }
            else {
                logger.warn("Failed to update course {} as course is deleted", code);
                throw new DeletedCourseUpdateException("Course is deleted and can only be restored by an admin");
            }
        }



        if(!hasRole(user,RoleType.ADMIN)&&!checkIfCourseBelongsToThatInstructor(user,course)){
            logger.warn("Failed to update course as course {} doesn't belong to instructor {}",code,user.getAssignedId());
            throw new UnauthorizedCourseUpdateException("This is not your course to update");
        }
        if(coursePhase!=null && !coursePhase.equals(CoursePhase.DELETED)){
            if(coursePhase==CoursePhase.ENROLLMENT){
                List<EnrolledCourse> checker=enrolledCourseService.getListOfStudentsOfCourse(course.getId(), EnrollmentStatus.ACTIVE);
                System.out.println(checker.size());
                if( checker.size()!=0  ){
                    logger.warn("Failed to open course for enrollment again");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("There are already students of this course u cannot open enrollment unless the course is finished");
                }
            }
            course.setCoursePhase(coursePhase);
        }
        if(courseAttributes!=null ) {

            if (courseAttributes.containsKey("code")) {
                if (checkIfCourseCodeIsTaken(courseAttributes.get("code"))) {
                    logger.warn("Failed to update course as code {} is already taken", courseAttributes.get("code"));
                    throw new CourseCodeAlreadyTakenException("This course code is already taken, choose another one.");

                }
            }
            if (courseAttributes.containsKey("title")) {
                if (checkIfCourseTitleIsTaken(courseAttributes.get("title"))) {
                    logger.warn("Failed to update course as title: {} is already taken", courseAttributes.get("title"));
                    throw new CourseTitleAlreadyTakenException("This course title is already taken, choose another one.");

                }
            }


            courseAttributes.forEach((k, v) -> updateCourseHelper(course, k, v));
        }
        if((coursePhase!=null && !coursePhase.equals(CoursePhase.DELETED)) || (courseAttributes!=null && !courseAttributes.isEmpty())) {
            course.setUpdatedAt(LocalDateTime.now());
            courseService.save(course);
        }
        if(courseAttributes!=null && !courseAttributes.isEmpty()){
            logger.info("Course {} updated successfully. Changed fields: {} ,course phase :{}", course.getCode(), courseAttributes.keySet(),course.getCoursePhase());
        }
        else
        logger.info("Course {} updated successfully ,course phase :{}", course.getCode(),course.getCoursePhase());

        return ResponseEntity.ok("Course: "+ code+" updated successfully");

    }
    @Transactional

     public ResponseEntity<?> deleteCourse(String token, String code){

        User user=extractUser(token);

         Course course= courseService.getCourseByCode(code);
            checkIfCourseIsNull(course);

         if(checkIfCourseIsDeleted(course)){
             logger.warn("Failed to delete course {} as it is deleted already",code);
             throw new DeletedCourseUpdateException("Course is already deleted and can only be restored by an admin");
         }



         if(!hasRole(user,RoleType.ADMIN)&&!checkIfCourseBelongsToThatInstructor(user,course)){
             logger.warn("Failed to delete course as course {} doesn't belong to instructor {}",code,user.getAssignedId());
             throw new UnauthorizedCourseUpdateException("This is not your course to update");
         }

         course.setCoursePhase(CoursePhase.DELETED);
         courseService.save(course);

         logger.info("Successfully deleted course {}",course.getCode());
         return ResponseEntity.ok("Course: "+ code+" deleted successfully");

     }


    public  ResponseEntity<?> finishCourse(String token,String courseCode){
        User user=extractUser(token);
        if(!hasRole(user,RoleType.ADMIN)){
            logger.warn("Failed to finish course {} as user not an admin",courseCode);
            throw new UnauthorizedCourseUpdateException("This is not an admin to finish course");
        }
        Course course= courseService.getCourseByCode(courseCode);
        checkIfCourseIsNull(course);
        addsUp(course.getAssessments());
        List<EnrolledCourse> activeStudents=enrolledCourseService.getListOfStudentsOfCourse(course.getId(),EnrollmentStatus.ACTIVE);
        activeStudents.forEach(as->gradeStudents(as,course.getAssessments()));
        deleteCourseFiles(course);
        course.setCoursePhase(CoursePhase.IN_ACTIVE);
        deleteFromDb(course);
        course.setEnrollmentCount(0);
        courseService.save(course);

        return ResponseEntity.ok().body("Course finished successfully");
     }









}
