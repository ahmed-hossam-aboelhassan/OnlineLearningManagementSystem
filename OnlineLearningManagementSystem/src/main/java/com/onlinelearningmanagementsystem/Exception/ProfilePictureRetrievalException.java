package com.onlinelearningmanagementsystem.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // Maps exception to 500
public class ProfilePictureRetrievalException extends RuntimeException {
  public ProfilePictureRetrievalException(String message) {
    super(message);
  }

  public ProfilePictureRetrievalException(String message, Throwable cause) {
    super(message, cause);
  }
}
