package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.EnrolledCourseQueryControllerService;
import com.onlinelearningmanagementsystem.Enum.EnrollmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EnrolledCourseQueryController {
    @Autowired
    EnrolledCourseQueryControllerService enrolledCourseQueryControllerService;

    @GetMapping("/enrolled")
    public ResponseEntity<PagedModel<?>> getCourses(@RequestHeader("Authorization") String token, @RequestParam(value = "studentId",required = false) String studentId, @RequestParam(value = "courseCode",required = false) String courseCode, @RequestParam(value = "enrollmentStatus",defaultValue = "ACTIVE") EnrollmentStatus enrollmentStatus, @RequestParam(value = "page",defaultValue ="0" ) int page){
    return enrolledCourseQueryControllerService.fetchEnrollmentData(token,studentId,courseCode,enrollmentStatus,page);
    }

}
