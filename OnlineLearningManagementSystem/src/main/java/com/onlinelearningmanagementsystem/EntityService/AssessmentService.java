package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Dao.AssessmentDao;
import com.onlinelearningmanagementsystem.Entity.Assessment;
import com.onlinelearningmanagementsystem.Enum.CriticalBonus;
import com.onlinelearningmanagementsystem.Mapper.AssessmentMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentService implements AssessmentServiceInterface {
    @Autowired
    AssessmentDao assessmentDao;


    public List<Assessment> getAllAssessmentsOfCourse(Long id){
        return assessmentDao.getAllAssessmentsOfCourse(id);
    }
    public List<Assessment> getAllCriticalAssessmentOfCourse(Long id){
        return assessmentDao.getAllCriticalAssessmentOfCourse(id);
    }
    public Assessment getAssessmentById(String id){
        return assessmentDao.findById(id).orElse(null);
    }
    public Page<Assessment> getPageOfAllAssessmentsOfCourse(Long id,int page){
        Pageable pageable= PageRequest.of(page,2);
        return assessmentDao.getPageOfAllAssessmentsOfCourse(id,pageable);
    }


    @Transactional
    public void deleteAssessmentById(Assessment assessment){
        assessmentDao.deleteAssessmentById(assessment.getId());
    }

    @Transactional
    public void deleteAssessmentsByCourseId(Long id){
        assessmentDao.deleteAssessmentByCourseId(id);
    }
    @Transactional
    public void save(Assessment assessment){
        assessmentDao.save(assessment);
    }
}
