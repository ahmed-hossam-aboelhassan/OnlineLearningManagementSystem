package com.onlinelearningmanagementsystem.ControllerAdvicer;

import com.onlinelearningmanagementsystem.Exception.*;
import com.onlinelearningmanagementsystem.ExceptionResponse.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "error", ex.getMessage(),
                        "status", status.value(),
                        "timestamp", LocalDateTime.now()
                )
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(TrackerNotInitializedException.class)
    public ResponseEntity<Map<String, Object>> handleTrackerNotInitializedException(TrackerNotInitializedException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFileException(InvalidFileException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleFileUploadException(FileUploadException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProfilePictureNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProfilePictureNotFoundException(ProfilePictureNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfilePictureRetrievalException.class)
    public ResponseEntity<Map<String, Object>> handleProfilePictureRetrievalException(ProfilePictureRetrievalException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidProfileUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidProfileUpdateException(InvalidProfileUpdateException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CourseCodeAlreadyTakenException.class)
    public ResponseEntity<Map<String, Object>> handleCourseCodeAlreadyTakenException(CourseCodeAlreadyTakenException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseTitleAlreadyTakenException.class)
    public ResponseEntity<Map<String, Object>> handleCourseTitleAlreadyTakenException(CourseTitleAlreadyTakenException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EmptyInstructorIdException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyInstructorIdException(EmptyInstructorIdException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInstructorIdException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInstructorIdException(InvalidInstructorIdException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnauthorizedCourseUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedCourseUpdateException(UnauthorizedCourseUpdateException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DeletedCourseUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleDeletedCourseUpdateException(DeletedCourseUpdateException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotFoundException(CourseNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(StudentIdEmptyException.class)
    public ResponseEntity<Map<String, Object>> handleStudentIdEmptyException(StudentIdEmptyException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentIdInvalidException.class)
    public ResponseEntity<Map<String, Object>> handleStudentIdInvalidException(StudentIdInvalidException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseNotInEnrollmentPhaseException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotInEnrollmentPhaseException(CourseNotInEnrollmentPhaseException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(StudentCompletedTheCourseException.class)
    public ResponseEntity<Map<String, Object>> handleStudentCompletedTheCourseException(StudentCompletedTheCourseException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StudentNotEnrolledInCourseException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotEnrolledInCourseException(StudentNotEnrolledInCourseException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StudentAlreadyEnrolledInCourseException.class)
    public ResponseEntity<Map<String, Object>> handleStudentAlreadyEnrolledInCourseException(StudentAlreadyEnrolledInCourseException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(CoursesNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCoursesNotFoundException(CoursesNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MissingParametersException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParametersException(MissingParametersException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(CourseNotActiveException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotActiveException(CourseNotActiveException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(CourseWeightException.class)
    public ResponseEntity<Map<String, Object>> handleCourseWeightException(CourseWeightException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DeadLineException.class)
    public ResponseEntity<Map<String, Object>> handleDeadLineException(DeadLineException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AssessmentUpdatesException.class)
    public ResponseEntity<Map<String, Object>> handleAssessmentUpdatesException(AssessmentUpdatesException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AssessmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAssessmentNotFoundException(AssessmentNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWeightInputException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidWeightInputException(InvalidWeightInputException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidDateTimeFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDateTimeFormatException(InvalidDateTimeFormatException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> handleFileStorageException(FileStorageException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(FileDeleteException.class)
    public ResponseEntity<Map<String, Object>> handleFileDeleteException(FileDeleteException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AssessmentResubmissionException.class)
    public ResponseEntity<Map<String, Object>> handleAssessmentResubmissionException(AssessmentResubmissionException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SubmittedAssessmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSubmittedAssessmentNotFoundException(SubmittedAssessmentNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(GradeException.class)
    public ResponseEntity<Map<String, Object>> handleGradeException(GradeException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(ErrorLoadingFileException.class)
    public ResponseEntity<Map<String, Object>> handleErrorLoadingFileException(ErrorLoadingFileException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.I_AM_A_TEAPOT);
    }








}
