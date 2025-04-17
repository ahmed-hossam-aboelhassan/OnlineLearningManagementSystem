package com.onlinelearningmanagementsystem.Mapper;

import com.onlinelearningmanagementsystem.Dto.NewAssessmentDto;
import com.onlinelearningmanagementsystem.Dto.NewCourseRequest;
import com.onlinelearningmanagementsystem.Entity.Assessment;
import com.onlinelearningmanagementsystem.Entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {
    Assessment toAssessment(NewAssessmentDto newAssessmentDto);
}
