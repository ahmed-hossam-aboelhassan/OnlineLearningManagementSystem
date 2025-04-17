package com.onlinelearningmanagementsystem.Exception;

public class StudentAlreadyEnrolledInCourseException extends RuntimeException {
    public StudentAlreadyEnrolledInCourseException(String message) {
        super(message);
    }
}
