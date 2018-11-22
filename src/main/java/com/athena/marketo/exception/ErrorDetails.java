package com.athena.marketo.exception;

import java.time.LocalDateTime;

public class ErrorDetails {

	  private LocalDateTime timestamp;
	  private String message;
	  private String details;

	  public ErrorDetails(String message, String details) {
	    super();
	    this.timestamp = LocalDateTime.now();
	    this.message = message;
	    this.details = details;
	  }
}
