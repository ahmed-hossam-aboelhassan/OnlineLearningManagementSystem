package com.onlinelearningmanagementsystem.Controller;


import com.onlinelearningmanagementsystem.ControllerService.AssessmentQueryControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentQueryController {

    @Autowired
    AssessmentQueryControllerService assessmentQueryControllerService;

    @GetMapping
    public ResponseEntity<PagedModel<?>> getAssessmentsOfCourse(@RequestHeader("Authorization") String token, @RequestParam("courseCode")String courseCode,@RequestParam(value = "page",defaultValue = "0") int page){
        return assessmentQueryControllerService.getAssessmentsByCourseCode(token,courseCode,page);
    }

   @GetMapping("{assessmentId}")
    public ResponseEntity<?> getAssessment(@RequestHeader("Authorization") String token,@PathVariable String assessmentId){
        return assessmentQueryControllerService.getAssessment(token,assessmentId);
   }
}
