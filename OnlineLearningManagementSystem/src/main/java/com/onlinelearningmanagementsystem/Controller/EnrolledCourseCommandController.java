package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.EnrolledCourseCommandControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EnrolledCourseCommandController {
    @Autowired
    EnrolledCourseCommandControllerService enrolledCourseCommandControllerService;

    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@RequestHeader("Authorization") String token,@RequestParam(value = "courseCode", required = true) String courseCode,@RequestParam(value = "studentId", required = false)String studentId){
        return enrolledCourseCommandControllerService.enroll(token,courseCode,studentId);
    }
    @PutMapping("/re_enroll")
    public ResponseEntity<?> reEnroll(@RequestHeader("Authorization") String token,@RequestParam(value = "courseCode", required = true) String courseCode,@RequestParam(value = "studentId", required = false)String studentId){
        return enrolledCourseCommandControllerService.reEnroll(token,courseCode,studentId);
    }

    @DeleteMapping("/un_enroll")
    public ResponseEntity<?> un_enroll(@RequestHeader("Authorization") String token,@RequestParam(value = "courseCode", required = true) String courseCode,@RequestParam(value = "studentId", required = false)String studentId){
        return enrolledCourseCommandControllerService.unenroll(token,courseCode,studentId);
    }


}
