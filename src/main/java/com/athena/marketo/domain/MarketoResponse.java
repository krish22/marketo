package com.athena.marketo.domain;

import java.util.List;

public class MarketoResponse {

	private String requestId;
	private boolean success;
	private List<MarketoResult> result;
	private List<MarketoError>  errors;
	
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/**
	 * @return the result
	 */
	public List<MarketoResult> getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(List<MarketoResult> result) {
		this.result = result;
	}
	/**
	 * @return the errors
	 */
	public List<MarketoError> getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<MarketoError> errors) {
		this.errors = errors;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MarketoResponse [requestId=" + requestId + ", success=" + success + ", result=" + result + ", errors="
				+ errors + "]";
	}
	
	
}
