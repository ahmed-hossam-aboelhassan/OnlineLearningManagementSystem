package com.onlinelearningmanagementsystem.EntityService;

import com.onlinelearningmanagementsystem.Entity.Assessment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssessmentServiceInterface {
    public List<Assessment> getAllAssessmentsOfCourse(Long id);
    public List<Assessment> getAllCriticalAssessmentOfCourse(Long id);
    public Assessment getAssessmentById(String id);
    public Page<Assessment> getPageOfAllAssessmentsOfCourse(Long id, int page);


    @Transactional
    public void deleteAssessmentById(Assessment assessment);

    @Transactional
    public void save(Assessment assessment);
    @Transactional
    void deleteAssessmentsByCourseId(Long id);

}
