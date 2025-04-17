package com.onlinelearningmanagementsystem.Exception;

public class CourseNotInEnrollmentPhaseException extends RuntimeException {
    public CourseNotInEnrollmentPhaseException(String message) {
        super(message);
    }
}
