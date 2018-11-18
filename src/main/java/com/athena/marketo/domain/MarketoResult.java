package com.athena.marketo.domain;

public class MarketoResult {

	private String exportId;
	private String status;
	private String createdAt;
	private String queuedAt;
	private String format;
	
	//These two are used for Lead Import response.
	private String batchId;
	private String importId;
	private int numOfLeadsProcessed;
	private int numOfRowsFailed;
	private int numOfRowsWithWarning;
	private String message;
	
	/**
	 * @return the exportId
	 */
	public String getExportId() {
		return exportId;
	}
	/**
	 * @return the numOfLeadsProcessed
	 */
	public int getNumOfLeadsProcessed() {
		return numOfLeadsProcessed;
	}
	/**
	 * @param numOfLeadsProcessed the numOfLeadsProcessed to set
	 */
	public void setNumOfLeadsProcessed(int numOfLeadsProcessed) {
		this.numOfLeadsProcessed = numOfLeadsProcessed;
	}
	/**
	 * @return the numOfRowsFailed
	 */
	public int getNumOfRowsFailed() {
		return numOfRowsFailed;
	}
	/**
	 * @param numOfRowsFailed the numOfRowsFailed to set
	 */
	public void setNumOfRowsFailed(int numOfRowsFailed) {
		this.numOfRowsFailed = numOfRowsFailed;
	}
	/**
	 * @return the numOfRowsWithWarning
	 */
	public int getNumOfRowsWithWarning() {
		return numOfRowsWithWarning;
	}
	/**
	 * @param numOfRowsWithWarning the numOfRowsWithWarning to set
	 */
	public void setNumOfRowsWithWarning(int numOfRowsWithWarning) {
		this.numOfRowsWithWarning = numOfRowsWithWarning;
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
	

	/**
	 * @return the batchId
	 */
	public String getBatchId() {
		return batchId;
	}
	/**
	 * @param batchId the batchId to set
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	/**
	 * @return the importId
	 */
	public String getImportId() {
		return importId;
	}
	/**
	 * @param importId the importId to set
	 */
	public void setImportId(String importId) {
		this.importId = importId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MarketoResult [exportId=" + exportId + ", status=" + status + ", createdAt=" + createdAt + ", queuedAt="
				+ queuedAt + ", format=" + format + ", batchId=" + batchId + ", importId=" + importId
				+ ", numOfLeadsProcessed=" + numOfLeadsProcessed + ", numOfRowsFailed=" + numOfRowsFailed
				+ ", numOfRowsWithWarning=" + numOfRowsWithWarning + ", message=" + message + "]";
	}
	
}
