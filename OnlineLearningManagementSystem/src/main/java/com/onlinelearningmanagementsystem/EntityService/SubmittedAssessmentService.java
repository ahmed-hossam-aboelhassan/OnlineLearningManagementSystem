package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.SubmittedAssessmentDao;
import com.onlinelearningmanagementsystem.Entity.SubmittedAssessment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmittedAssessmentService implements SubmittedAssessmentServiceInterface {

    @Autowired
    private SubmittedAssessmentDao submittedAssessmentDao;

    public SubmittedAssessment getSubmittedAssessment(String submittedAssessmentId){
        return submittedAssessmentDao.findById(submittedAssessmentId).orElse(null);
    }

    public SubmittedAssessment getSubmittedAssessment(String assessmentId,String studentId){
        return submittedAssessmentDao.getSubmittedAssessment(assessmentId,studentId);
    }
    public List<SubmittedAssessment>getSubmittedAssessmentsForStudentInList(Long courseId,String studentId){
        return submittedAssessmentDao.getSubmittedAssessmentsForStudentInList(courseId,studentId);
    }

    public Page<SubmittedAssessment> getSubmittedAssessmentsForStudent(Long courseId,String studentId,int page){
        Pageable pageable= PageRequest.of(page,4);
        return submittedAssessmentDao.getSubmittedAssessmentsForStudent(courseId,studentId,pageable);
    }
    public Page<SubmittedAssessment> getSubmittedAssessmentsForInstructorByAssessmentId(String assessmentId,int page){
        Pageable pageable= PageRequest.of(page,4);
        return submittedAssessmentDao.getSubmittedAssessmentsForInstructorByAssessmentId(assessmentId,pageable);
    }


    @Transactional
    public void save(SubmittedAssessment submittedAssessment){
        submittedAssessmentDao.save(submittedAssessment);
    }

    @Transactional
    public void delete(String id){
        submittedAssessmentDao.deleteSubmissionById(id);
    }

    @Transactional
    public void deleteByAssessmentId(String id){
        submittedAssessmentDao.deleteSubmissionByAssessmentId(id);
    }


}
