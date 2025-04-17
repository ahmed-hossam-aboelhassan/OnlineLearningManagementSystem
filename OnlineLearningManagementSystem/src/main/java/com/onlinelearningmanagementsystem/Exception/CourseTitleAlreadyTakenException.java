package com.onlinelearningmanagementsystem.Exception;

public class CourseTitleAlreadyTakenException extends RuntimeException {
    public CourseTitleAlreadyTakenException(String message) {
        super(message);
    }
}
