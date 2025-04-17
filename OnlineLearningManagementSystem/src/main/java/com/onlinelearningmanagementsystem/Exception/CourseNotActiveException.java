package com.onlinelearningmanagementsystem.Exception;

public class CourseNotActiveException extends RuntimeException {
    public CourseNotActiveException(String message) {
        super(message);
    }
}
