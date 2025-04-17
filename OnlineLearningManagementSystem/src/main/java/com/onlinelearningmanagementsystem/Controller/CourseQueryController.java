package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.CourseQueryControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
public class CourseQueryController {

    @Autowired
    private CourseQueryControllerService courseQueryControllerService;

    @GetMapping
    public ResponseEntity<?> getAllCourses(@RequestHeader(value = "Authorization", required = false) String token) {
        return courseQueryControllerService.getAllCourses(token);
    }



    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getCoursesByCategory(@PathVariable String categoryName,@RequestHeader(value="Authorization", required = false)String token) {
        return courseQueryControllerService.getCoursesByCategory(categoryName,token);
    }


    @GetMapping("/title/{title}")
    public ResponseEntity<?> getCoursesByTitle(@PathVariable String title,@RequestHeader(value="Authorization", required = false)String token) {
        return courseQueryControllerService.getCourseByTitle(title,token);

    }
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getCourseByCode(@PathVariable String code,@RequestHeader(value="Authorization", required = false)String token) {
        return courseQueryControllerService.getCourseByCode(code,token);

    }
    @GetMapping("/of/{instructorId}")
    public ResponseEntity<?> getCoursesByInstructor(@PathVariable String instructorId,@RequestHeader(value="Authorization", required = false)String token) {
        return courseQueryControllerService.getCoursesByInstructorId(instructorId, token);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getCourseOfInstructor(@RequestHeader("Authorization")String token) {
        return courseQueryControllerService.getCoursesOfInstructor(token);

    }



    //    @GetMapping("/{courseId}")
//    public ResponseEntity<?> getCourseById(@PathVariable Long courseId) {
//        // Fetch course by ID
//    }

}


