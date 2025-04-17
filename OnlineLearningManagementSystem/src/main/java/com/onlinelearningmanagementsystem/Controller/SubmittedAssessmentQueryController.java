package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.SubmittedAssessmentQueryControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submitted-assessment")
public class SubmittedAssessmentQueryController {
    @Autowired
    private SubmittedAssessmentQueryControllerService submittedAssessmentQueryControllerService;

    @GetMapping
    public ResponseEntity<?> getSubmissions(@RequestHeader("Authorization") String token, @RequestParam(value = "courseCode",required = false)String courseCode,@RequestParam(value = "assessmentId",required = false)String assessmentId,@RequestParam(value = "page",defaultValue = "0")int page){
    return submittedAssessmentQueryControllerService.getSubmissions(token,courseCode,assessmentId,page);
    }

    @GetMapping("/{submitted-assessment-id}")
    public ResponseEntity<?> getSubmittedAssessment(@RequestHeader("Authorization") String token,@PathVariable("submitted-assessment-id") String submittedAssessmentId){
        return submittedAssessmentQueryControllerService.getSubmittedAssessment(token,submittedAssessmentId);
    }
}
