package com.onlinelearningmanagementsystem.ControllerService;

import com.onlinelearningmanagementsystem.Dto.CourseViewDto;
import com.onlinelearningmanagementsystem.Dto.AuthorizedCourseViewDto;
import com.onlinelearningmanagementsystem.Entity.Course;
import com.onlinelearningmanagementsystem.Entity.Role;
import com.onlinelearningmanagementsystem.Entity.User;
import com.onlinelearningmanagementsystem.EntityService.CourseService;
import com.onlinelearningmanagementsystem.EntityService.UserService;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import com.onlinelearningmanagementsystem.Enum.RoleType;
import com.onlinelearningmanagementsystem.Exception.CourseNotFoundException;
import com.onlinelearningmanagementsystem.Exception.CoursesNotFoundException;
import com.onlinelearningmanagementsystem.JwtService.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseQueryControllerService {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(CourseQueryControllerService.class);

    private User extractUser(String token) {


        String jwtToken = token.substring(7);
        String id = jwtUtil.extractUsername(jwtToken);
        return userService.findUserById(id);
    }

    private boolean checkIfCourseIsNull(Course course){
        return course==null;
    }
    private boolean checkIfListOfCoursesIsNull(List<Course> courses){
        return courses==null||courses.size()==0;
    }
    private boolean checkIfCourseIsDeleted(Course course){
        return course.getCoursePhase().equals(CoursePhase.DELETED);
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
    private boolean tokenValidation(String token){
        if (token==null)
            return false;
        if(token.length()<7)
            return false;
        if(!token.startsWith("Bearer "))
            return false;
        if (token.split("\\.").length != 3) {
            System.out.println("meow");
            return false;
        }

        return true;


    }
    private ResponseEntity<?> possibleAdminViewCourses(User user,List<Course> courses){

        if(hasRole(user,RoleType.ADMIN)) {
            List<AuthorizedCourseViewDto>courseViewDtos=courses.stream()
                    .map(AuthorizedCourseViewDto::new)
                    .toList();
            if(courseViewDtos.isEmpty()){
                logger.info("Nothing found");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            }
            logger.info("Successfully fetched {} courses for {}", courses.size(),user.getAssignedId());
            return ResponseEntity.ok(courseViewDtos);

        }
        else {

            List<CourseViewDto> courseViewDtos = courses.stream()
                    .filter(course -> !course.getCoursePhase().equals(CoursePhase.DELETED))
                    .map(CourseViewDto::new)
                    .toList();
            if(courseViewDtos.isEmpty()){
                logger.info("Nothing found");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            }

            logger.info("Successfully fetched {} courses for {}", courses.size(),user.getAssignedId());
            return ResponseEntity.ok(courseViewDtos);
        }
    }
    private ResponseEntity<?> possibleAdminViewCourse(User user,Course course){

        if(hasRole(user,RoleType.ADMIN)) {
            AuthorizedCourseViewDto courseViewDto=new AuthorizedCourseViewDto(course);


            logger.info("Successfully found course {} for {}", course.getCode(),user.getAssignedId());
            return ResponseEntity.ok(courseViewDto);

        }
        else {

            CourseViewDto courseViewDto = new CourseViewDto(course);


            logger.info("Successfully found course for {} ", course.getCode(),user.getAssignedId());
            return ResponseEntity.ok(courseViewDto);
        }
    }

    public ResponseEntity<?> getAllCourses(String token) {


        List<Course> courses = courseService.getAllCourses();
        if (checkIfListOfCoursesIsNull(courses)) {
            logger.warn("Failed to get courses as no courses found");
            throw new CoursesNotFoundException("No courses found");
        }
        if(tokenValidation(token)){
            User user=extractUser(token);
            return possibleAdminViewCourses(user,courses);

        }
        else {

            List<CourseViewDto> courseViewDtos = courses.stream()
                    .filter(course -> !course.getCoursePhase().equals(CoursePhase.DELETED))
                    .map(CourseViewDto::new)
                    .toList();

            if(courseViewDtos.isEmpty()){
                logger.info("Nothing found");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            }
            logger.info("Successfully fetched {} courses", courses.size());
            return ResponseEntity.ok(courseViewDtos);
        }

    }

    public ResponseEntity<?> getCoursesByCategory(String category,String token) {


        List<Course> courses = courseService.getCourseByCategory(category);
        if (checkIfListOfCoursesIsNull(courses)) {
            logger.warn("Failed to get courses as no courses found in category: {}", category);
            throw new CoursesNotFoundException("No courses found");
        }

        if(tokenValidation(token)){
            User user=extractUser(token);
            return possibleAdminViewCourses(user,courses);

        }
        else {

            List<CourseViewDto> courseViewDtos = courses.stream()
                    .filter(course -> !course.getCoursePhase().equals(CoursePhase.DELETED))
                    .map(CourseViewDto::new)
                    .toList();

            if(courseViewDtos.isEmpty()){
                logger.info("Nothing found");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            }

            logger.info("Successfully fetched {} courses", courses.size());
            return ResponseEntity.ok(courseViewDtos);
        }
    }

    public ResponseEntity<?> getCoursesByInstructorId(String id,String token) {


        List<Course> courses = courseService.getCourseByInstructorId(id);
        if (checkIfListOfCoursesIsNull(courses)) {
            logger.warn("Failed to get courses as no courses found for instructor ID: {}", id);
            throw new CoursesNotFoundException("No courses found");
        }

        if(tokenValidation(token)){
            User user=extractUser(token);
            return possibleAdminViewCourses(user,courses);

        }
        else {

            List<CourseViewDto> courseViewDtos = courses.stream()
                    .filter(course -> !course.getCoursePhase().equals(CoursePhase.DELETED))
                    .map(CourseViewDto::new)
                    .toList();
            if(courseViewDtos.isEmpty()){
                logger.info("Nothing found");
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            }

            logger.info("Successfully fetched {} courses", courses.size());
            return ResponseEntity.ok(courseViewDtos);
        }
    }

    public ResponseEntity<?> getCourseByCode(String code,String token) {


        Course course = courseService.getCourseByCode(code);
        if (checkIfCourseIsNull(course)) {
            logger.warn("Failed to get courses as no course with code: {}", code);
            throw new CourseNotFoundException("Wrong code, please type a valid code");
        }
        if(tokenValidation(token)){
            User user=extractUser(token);
            return possibleAdminViewCourse(user,course);

        }
        else {
            if(checkIfCourseIsDeleted(course)){
                logger.warn("Failed to get courses as no course with code: {}", code);
                throw new CourseNotFoundException("Wrong code, please type a valid code");
            }

            logger.info("Successfully found course with code: {}", code);
            return ResponseEntity.ok(new CourseViewDto(course));
        }
    }

    public ResponseEntity<?> getCourseByTitle(String title,String token) {


        Course course = courseService.getCourseByTitle(title);
        if (checkIfCourseIsNull(course)) {
            logger.warn("Failed to get courses as no course with title: {}", title);
            throw new CourseNotFoundException("Wrong title, please type a valid title");
        }
        if(tokenValidation(token)){
            User user=extractUser(token);
            return possibleAdminViewCourse(user,course);

        }
        else {
            if(checkIfCourseIsDeleted(course)){
                logger.warn("Failed to get courses as no course with title: {}", title);
                throw new CourseNotFoundException("Wrong title, please type a valid title");
            }

            logger.info("Successfully found course with title: {}", title);
            return ResponseEntity.ok(new CourseViewDto(course));
        }
    }

    public ResponseEntity<?> getCoursesOfInstructor(String token) {

        User user = extractUser(token);
        List<Course> courses = user.getInstructorCourses();
        if (checkIfListOfCoursesIsNull(courses)) {
            logger.warn("Failed to get courses as no courses for {} yet", user.getAssignedId());
            throw new CoursesNotFoundException("No courses found");
        }

        List<AuthorizedCourseViewDto> authorizedCourseViewDtos = courses.stream()
                .map(AuthorizedCourseViewDto::new)
                .toList();
        if(authorizedCourseViewDtos.isEmpty()){
            logger.info("Nothing found");
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
        }

        logger.info("Successfully fetched {} courses for instructor {}", courses.size(), user.getAssignedId());
        return ResponseEntity.ok(authorizedCourseViewDtos);
    }

//    public ResponseEntity<?> getCoursesOfInstructorByAdmin(String id) {
//
//
//        User user = userService.findUserByAssignedId(id);
//        if (user == null) {
//            logger.warn("Failed to get courses as no instructor with  ID: {}", id);
//            return ResponseEntity.badRequest().body("Instructor ID is incorrect");
//        }
//
//        List<Course> courses = user.getInstructorCourses();
//        if (checkIfListOfCoursesIsNull(courses)) {
//            logger.warn("Failed to get courses as {} has no courses yet", id);
//            return ResponseEntity.badRequest().body("No courses have been created yet");
//        }
//
//        List<AuthorizedCourseViewDto> authorizedCourseViewDtos = courses.stream()
//                .map(AuthorizedCourseViewDto::new)
//                .toList();
//
//        logger.info("Admin successfully fetched {} courses for instructor {}", courses.size(), id);
//        return ResponseEntity.ok(authorizedCourseViewDtos);
//    }
    //    public ResponseEntity<?> getCourseById(Long id){
//        Course course=courseService.getCourse(id);
//        CourseViewDto courseViewDto;
//        if(!checkIfCourseIsNull(course)){
//            courseViewDto=new CourseViewDto(course);
//
//            return ResponseEntity.ok(courseViewDto);
//        }
//
//        return ResponseEntity.badRequest().body("Wrong id please type in a valid id");
//
//    }

}
