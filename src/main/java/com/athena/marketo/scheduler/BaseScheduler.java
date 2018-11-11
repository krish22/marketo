package com.athena.marketo.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.athena.marketo.service.MarketoClient;
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

	@Autowired
	private MarketoClient marketoClient;
	
	@Autowired
	private MethodPoller<ObjectNode> methodPoller;
	
	@Value("${exportfilepath}")
	private String exportfilepath;
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

	public abstract void run();
	
	protected abstract Map<String,Object> populateRequest();
	
	public String createExportJob(Map<String, Object> requestBody) {
		
		log.info("Create Job for Exporting {}", this.getAction());
		
		ObjectNode response = marketoClient.createJob(this.getCreateJobUrl(), requestBody);
		
		//TODO: remove the hard coded value and pass proper exportId from the response
		return "exportId";
	}
	
	public boolean processJob(String exportId) {

		//Step 2: Start a job
		String status = startExportJob(exportId);
		
		//Step 3 : polling job status
		boolean isSuccess = pollingExportJobStatus(exportId);
		
		//Step 3 : Retrieve file and store it into local disk
		retrieveExportData(exportId);
		
		return false;
	}
	
	protected String startExportJob(String exportId) {
		log.info("Start Job for Exporting {}", this.action);
		
		ObjectNode response = marketoClient.startJob(buildUrl(this.getStartJobUrl(),exportId));
		//TODO: remove the hard coded value and pass proper status from the response
		return "status";
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
	
	protected boolean pollingExportJobStatus(String exportId) {
		log.info("Polling Job status for Exporting {}", this.action);
		String url = buildUrl(this.getPollStatusJobUrl(), exportId);
		ObjectNode response = null;
		try {
			response = methodPoller.poll(1000)
								 	.method(()->marketoClient.pollingJobStatus(url))
								 	.until(resp -> "Completed".equals(resp.get("result").get("status").asText()) 
								 				|| "Cancelled".equals(resp.get("result").get("status").asText()) 
								 				|| "Failed".equals(resp.get("result").get("status").asText()))
								 	.execute();
		} catch (InterruptedException e) {
			log.error(e.getMessage(),e);
		}
		//TODO: remove the hard coded value and pass proper status from the response
		return response.get("result").get("status").asBoolean();
	}
	
	protected void retrieveExportData(String exportId) {
		log.info("Retrieving data for {}", this.action);
		String url = buildUrl(this.getRetrieveDataUrl(), exportId);
		try {
			InputStream reponseInputStream = marketoClient.retrieveExportData(url);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(reponseInputStream));
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(exportfilepath + "exportdata.csv")))){
				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line);
				}
			}
			reader.close();
			
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}
}
