package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.CourseCommandControllerService;
import com.onlinelearningmanagementsystem.Dto.NewCourseRequest;
import com.onlinelearningmanagementsystem.Enum.CoursePhase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course")
@Validated
public class CourseCommandController {

    @Autowired
    private CourseCommandControllerService courseCommandControllerService;

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestHeader("Authorization") String token, @Valid @RequestBody NewCourseRequest newCourseRequest){
        return courseCommandControllerService.createCourse(token, newCourseRequest);
    }



    @PutMapping("/{coursecode}")
    public ResponseEntity<?> updateCourse(@RequestHeader("Authorization") String token, @RequestBody(required = false) Map<String,String> courseAttributes,@PathVariable String coursecode,@RequestParam(required = false) CoursePhase coursePhase){
        return courseCommandControllerService.updateCourse(token,coursecode,courseAttributes,coursePhase);
    }

    @DeleteMapping("/{coursecode}")
    public ResponseEntity<?> deleteCourse(@RequestHeader("Authorization") String token,@PathVariable String coursecode){
        return courseCommandControllerService.deleteCourse(token,coursecode);
    }

    @PutMapping("/{coursecode}/finish")
    public ResponseEntity<?>finishCourse(@RequestHeader("Authorization") String token,@PathVariable String coursecode){
        return courseCommandControllerService.finishCourse(token,coursecode);
    }






}
