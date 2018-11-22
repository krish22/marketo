package com.athena.marketo.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.athena.marketo.domain.MarketoResponse;
import com.athena.marketo.exception.MarketoException;
import com.athena.marketo.service.MarketoClient;
import com.athena.marketo.utils.JsonUtils;
import com.athena.marketo.utils.MethodPoller;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class BaseScheduler {

	private static final Logger log = LoggerFactory.getLogger(BaseScheduler.class);
	
	private String createJobUrl;
	private String startJobUrl;
	private String pollStatusJobUrl;
	private String retrieveDataUrl;
	private String cancelJobUrl;
	private String action; 

	private String startAt;
	/**
	 * @return the startAt
	 */
	public String getStartAt() {
		return startAt;
	}

	/**
	 * @param startAt the startAt to set
	 */
	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	/**
	 * @return the endAt
	 */
	public String getEndAt() {
		return endAt;
	}

	/**
	 * @param endAt the endAt to set
	 */
	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

	private String endAt;
	
	@Autowired
	private MarketoClient marketoClient;
	
	@Autowired
	private MethodPoller<MarketoResponse> methodPoller;
	
	@Value("${exportfilepath}")
	private String exportfilepath;
	
	@Value("${extract_duration}")
	protected int extractDuration;
	
	/**
	 * @return the createJobUrl
	 */
	public String getCreateJobUrl() {
		return createJobUrl;
	}

	/**
	 * @param createJobUrl the createJobUrl to set
	 */
	public void setCreateJobUrl(String createJobUrl) {
		this.createJobUrl = createJobUrl;
	}

	/**
	 * @return the startJobUrl
	 */
	public String getStartJobUrl() {
		return startJobUrl;
	}

	/**
	 * @param startJobUrl the startJobUrl to set
	 */
	public void setStartJobUrl(String startJobUrl) {
		this.startJobUrl = startJobUrl;
	}

	/**
	 * @return the pollStatusJobUrl
	 */
	public String getPollStatusJobUrl() {
		return pollStatusJobUrl;
	}

	/**
	 * @param pollStatusJobUrl the pollStatusJobUrl to set
	 */
	public void setPollStatusJobUrl(String pollStatusJobUrl) {
		this.pollStatusJobUrl = pollStatusJobUrl;
	}

	/**
	 * @return the retrieveDataUrl
	 */
	public String getRetrieveDataUrl() {
		return retrieveDataUrl;
	}

	/**
	 * @param retrieveDataUrl the retrieveDataUrl to set
	 */
	public void setRetrieveDataUrl(String retrieveDataUrl) {
		this.retrieveDataUrl = retrieveDataUrl;
	}
	
	/**
	 * @return the cancelJobUrl
	 */
	public String getCancelJobUrl() {
		return cancelJobUrl;
	}

	/**
	 * @param cancelJobUrl the cancelJobUrl to set
	 */
	public void setCancelJobUrl(String cancelJobUrl) {
		this.cancelJobUrl = cancelJobUrl;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public abstract void run(String startAt, String endAt) throws MarketoException;
	
	protected abstract ObjectNode populateRequest() throws ParseException;
	
	public String createExportJob(ObjectNode requestBody) throws MarketoException {
		
		log.info("Create Job for Exporting {}", this.getAction());
		
		String exportId = marketoClient.createJob(this.getCreateJobUrl(), requestBody);
		
		log.info("Job created Successfully and the export id is : {}",exportId);
		return exportId;
	}
	
	public boolean processJob(String exportId) throws MarketoException {

		//Step 2: Start a job
		String status = startExportJob(exportId);
		
		//Step 3 : polling job status
		MarketoResponse response = pollingExportJobStatus(exportId);
		
		//Step 3 : Retrieve file and store it into local disk
		if("Completed".equals(response.getResult().get(0).getStatus())) {
			retrieveExportData(exportId);
		}
		
		return false;
	}
	
	protected String startExportJob(String exportId) throws MarketoException {
		log.info("Start Job for Exporting {}", this.action);
		
		MarketoResponse response = marketoClient.startJob(buildUrl(this.getStartJobUrl(),exportId));
		return response.getResult().get(0).getStatus();
	}

	/**
	 * @param url
	 * @param exportId
	 * @return
	 */
	private String buildUrl(String url,String exportId) {
		Map<String, String> pathParam = new HashMap<>();
		pathParam.put("exportId", exportId);
		UriComponents uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(pathParam);
		return uri.toString();
	}
	
	protected MarketoResponse pollingExportJobStatus(String exportId) {
		log.info("Polling Job status for Exporting {}", this.action);
		String url = buildUrl(this.getPollStatusJobUrl(), exportId);
		MarketoResponse response = null;
		try {
			response = methodPoller.poll(1000*30)
								 	.method(()->{
										try {
											return marketoClient.pollingJobStatus(url);
										} catch (MarketoException e) {
											log.error(e.getCode(),e.getMessage(),e);
										}
										return null;
									})
								 	.until(resp -> "Completed".equals(resp.getResult().get(0).getStatus())
								 				|| "Cancelled".equals(resp.getResult().get(0).getStatus()) 
								 				|| "Failed".equals(resp.getResult().get(0).getStatus()))
								 	.execute();
		} catch (InterruptedException e) {
			log.error(e.getMessage(),e);
		}
		
		return response;
	}
	
	protected void retrieveExportData(String exportId) {
		log.info("Retrieving data for {}", this.action);
		String url = buildUrl(this.getRetrieveDataUrl(), exportId);
		try {
			InputStream reponseInputStream = marketoClient.retrieveFileData(url);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(reponseInputStream));
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
			// This will create an file with name 2018-01-31T13:09:00_Lead.csv
			StringBuilder filePath = new StringBuilder(exportfilepath)
							.append(dateFormat.format(Calendar.getInstance().getTime()))
							.append("_")
							.append(this.action)
							.append(".csv");
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath.toString())))){
				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line);
					writer.write("\n");
				}
			}
			reader.close();
			
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public ObjectNode createdAtFilter(String startAt, String endAt) throws ParseException {
		ObjectNode createdAtFilter = JsonUtils.objectNode();
		
		/*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Calendar startDate = Calendar.getInstance();

		setTimeToZero(startDate);
		Calendar endDate = Calendar.getInstance();
		startDate.set(Calendar.DATE, startDate.get(Calendar.DATE)- extractDuration);
		setTimeToZero(endDate);
		
		dateFormat.parse(startAt);
		dateFormat.parse(endAt);*/
		
		createdAtFilter.put("startAt",startAt);
		createdAtFilter.put("endAt", endAt);
		
		log.info(" created at filterValue {}", String.valueOf(createdAtFilter));
		return createdAtFilter;
		
	}
	
	/**
	 * This method will set time to 00:00:00
	 * @param date
	 */
	private void setTimeToZero(Calendar date) {
		date.set(Calendar.HOUR_OF_DAY, 0);;
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND,0);
	}

	protected boolean validDateFormat(String starAt,String endAt) throws MarketoException {
		String dateFormatStr = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
		try {
			dateFormat.parse(starAt);
			dateFormat.parse(endAt);
		} catch (ParseException e) {
			log.error("Inavlid data format, it should be {}",dateFormatStr);
			log.error(e.getMessage(),e);
			throw new MarketoException("524", "Inavlid data format, it should be " + dateFormatStr, e);
		}
		
		return true;
	}
}
