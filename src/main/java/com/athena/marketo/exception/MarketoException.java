package com.athena.marketo.exception;

public class MarketoException extends Exception {

	public MarketoException(String message) {
		super(message);
	}

	public MarketoException(Throwable cause) {
		super(cause);
	}

	public MarketoException(String message, Throwable cause) {
		super(message, cause);
	}

	public MarketoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
