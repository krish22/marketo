package com.athena.marketo.exception;

public class MarketoException extends Exception {
	
	private String code;
	private String message;

	public MarketoException(String code, String message) {
		super(message);
	}

	public MarketoException(Throwable cause) {
		super(cause);
	}

	public MarketoException(String code,String message, Throwable cause) {
		super(message, cause);
	}

	public MarketoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
