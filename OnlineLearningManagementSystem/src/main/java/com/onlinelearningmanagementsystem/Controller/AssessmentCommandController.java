package com.onlinelearningmanagementsystem.Controller;

import com.onlinelearningmanagementsystem.ControllerService.AssessmentCommandControllerService;
import com.onlinelearningmanagementsystem.Dto.NewAssessmentDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/assessment")
@Validated
public class AssessmentCommandController {

    @Autowired
    AssessmentCommandControllerService assessmentCommandControllerService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadAssessment(@RequestHeader("Authorization")String token, @RequestPart(value = "file",required = true)MultipartFile file, @RequestPart(value="newAssessmentDto",required = true)@Valid  NewAssessmentDto newAssessmentDto, @RequestParam(value = "courseCode",required = true)String courseCode){
    return assessmentCommandControllerService.uploadAssessment(token,courseCode,file,newAssessmentDto);
    }

    @PutMapping("/{assessmentId}")
    public ResponseEntity<?>updateAssessment(@RequestHeader("Authorization")String token, @PathVariable String assessmentId, @RequestBody Map<String,Object>updates){
        return assessmentCommandControllerService.updateAssessment(token,assessmentId,updates);
    }

    @DeleteMapping("/{assessmentId}")
    public ResponseEntity<?>deleteAssessment(@RequestHeader("Authorization")String token, @PathVariable String assessmentId){
        return assessmentCommandControllerService.deleteAssessment(token,assessmentId);
    }


}
