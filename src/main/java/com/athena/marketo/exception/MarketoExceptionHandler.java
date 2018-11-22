package com.athena.marketo.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class MarketoExceptionHandler extends ResponseEntityExceptionHandler{

	
	@ExceptionHandler(MarketoException.class)
	protected ResponseEntity<Object> handlerMarketoException(MarketoException e, WebRequest request){
		return buildResponseEntity(e,request);
	}
	
	
	private ResponseEntity<Object> buildResponseEntity(MarketoException errorResponse,WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");
        ErrorDetails errorDetails = new ErrorDetails(errorResponse.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
}
