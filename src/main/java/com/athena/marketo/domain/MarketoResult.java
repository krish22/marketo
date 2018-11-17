package com.athena.marketo.domain;

public class MarketoResult {

	private String exportId;
	private String status;
	private String createdAt;
	private String queuedAt;
	private String format;
	
	/**
	 * @return the exportId
	 */
	public String getExportId() {
		return exportId;
	}
	/**
	 * @param exportId the exportId to set
	 */
	public void setExportId(String exportId) {
		this.exportId = exportId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the createdAt
	 */
	public String getCreatedAt() {
		return createdAt;
	}
	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @return the queuedAt
	 */
	public String getQueuedAt() {
		return queuedAt;
	}
	/**
	 * @param queuedAt the queuedAt to set
	 */
	public void setQueuedAt(String queuedAt) {
		this.queuedAt = queuedAt;
	}
	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
}
