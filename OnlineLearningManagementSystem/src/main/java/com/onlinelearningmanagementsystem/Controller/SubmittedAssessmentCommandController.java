package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.SubmittedAssessmentCommandControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/submitted-assessment")
public class SubmittedAssessmentCommandController {
    @Autowired
    private SubmittedAssessmentCommandControllerService submittedAssessmentCommandControllerService;


    @PostMapping
    public ResponseEntity<?> submit(@RequestHeader("Authorization") String token, @RequestPart(value = "file")MultipartFile file,@RequestParam("assessmentId") String assessmentId){
        return submittedAssessmentCommandControllerService.submitAssessment(token,file,assessmentId);
    }
    @PutMapping
    public ResponseEntity<?> reSubmit(@RequestHeader("Authorization") String token, @RequestPart(value = "file")MultipartFile file,@RequestParam("assessmentId") String assessmentId){
        return submittedAssessmentCommandControllerService.reSubmit(token,assessmentId,file);
    }
    @PutMapping("/{submitted-assessment-id}")
    public ResponseEntity<?> updateGrade(@RequestHeader("Authorization") String token, @PathVariable("submitted-assessment-id") String submittedAssessmentId, @RequestParam(value = "grade") BigDecimal grade){
        return submittedAssessmentCommandControllerService.updateGrade(token,submittedAssessmentId,grade);
    }

}
