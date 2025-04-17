package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.SubmittedAssessment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubmittedAssessmentServiceInterface {
    public SubmittedAssessment getSubmittedAssessment(String submittedAssessmentId);

    public SubmittedAssessment getSubmittedAssessment(String assessmentId,String studentId);
    public List<SubmittedAssessment> getSubmittedAssessmentsForStudentInList(Long courseId, String studentId);

    public Page<SubmittedAssessment> getSubmittedAssessmentsForStudent(Long courseId, String studentId, int page);
    public Page<SubmittedAssessment> getSubmittedAssessmentsForInstructorByAssessmentId(String assessmentId,int page);


    @Transactional
    public void save(SubmittedAssessment submittedAssessment);

    @Transactional
    public void delete(String id);
    @Transactional
    public void deleteByAssessmentId(String id);
}
