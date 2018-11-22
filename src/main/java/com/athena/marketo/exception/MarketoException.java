package com.athena.marketo.exception;

public class MarketoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4067872527342710554L;
	
	private String code;
	private String message;

	public MarketoException(String code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public MarketoException(Throwable cause) {
		super(cause);
	}

	public MarketoException(String code,String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.message = message;
	}

	public MarketoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


	public MarketoException(String message) {
		super(message);
		this.message = message;
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
