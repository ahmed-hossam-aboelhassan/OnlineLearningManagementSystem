package com.onlinelearningmanagementsystem.Mapper;

import com.onlinelearningmanagementsystem.Dto.NewCourseRequest;
import com.onlinelearningmanagementsystem.Entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toCourse(NewCourseRequest request);
}

