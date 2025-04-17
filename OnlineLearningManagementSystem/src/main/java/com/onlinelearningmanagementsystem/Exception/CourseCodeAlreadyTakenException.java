package com.onlinelearningmanagementsystem.Exception;

public class CourseCodeAlreadyTakenException extends RuntimeException {
    public CourseCodeAlreadyTakenException(String message) {
        super(message);
    }
}
