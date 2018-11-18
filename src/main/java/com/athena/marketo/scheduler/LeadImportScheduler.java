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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.athena.marketo.domain.MarketoResponse;
import com.athena.marketo.exception.MarketoException;
import com.athena.marketo.service.MarketoClient;
import com.athena.marketo.utils.MarketoConstants;
import com.athena.marketo.utils.MethodPoller;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LeadImportScheduler {

	private static final Logger log = LoggerFactory.getLogger(LeadImportScheduler.class);
	
	@Autowired
	private MarketoClient marketoClient;

	@Autowired
	private MethodPoller<MarketoResponse> methodPoller;
	
	
	@Value("${exportfilepath}")
	private String exportfilepath;
	
	public void run(MultipartFile file) throws MarketoException {
		Runnable runnable = () -> {
			
			try {
				//Step 1 : Create job 
				MarketoResponse response =  createJob(file.getResource());
				
				//Step 2: polling status
				String batchId  = response.getResult().get(0).getBatchId();
				response = pollingStatus(batchId);
				
				//Step 3 : if there are any failures then get the failure records with reason
				if (response.getResult().get(0).getNumOfRowsFailed() > 0) {
					retrieveFailedData(batchId);
				}
				
				if (response.getResult().get(0).getNumOfRowsWithWarning() > 0) {
					retrieveWarningData(batchId);
				}
						
			} catch (MarketoException e) {
				log.error(e.getCode(),e.getMessage(),e);
				throw new RuntimeException("upload lead data failed");
			} catch (IOException e) {
				log.error(e.getMessage(),e);
				throw new RuntimeException("Failed to retrieve failed/warning data");
			}
		};
		
		Thread leadImportScheduler = new Thread(runnable);
		leadImportScheduler.start();
		
	}

	private void retrieveWarningData(String batchId) throws IOException {
		log.info("Inside retrieveWarningData");
		String url = buildUrl(MarketoConstants.WARNING_LEAD_IMPORT_JOB_URL, batchId);
		downloadFile(url, batchId+"_warning.csv");
		
	}

	private void retrieveFailedData(String batchId) throws IOException {
		log.info("Inside retrieveFailedData");
		String url = buildUrl(MarketoConstants.FAILURE_LEAD_IMPORT_JOB_URL, batchId);
		downloadFile(url, batchId+"_failure.csv");
		
		
	}
	
	private void downloadFile(String url,String fileName) throws IOException {
		log.info("Inside downloadFile");
		InputStream reponseInputStream = marketoClient.retrieveFileData(url);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(reponseInputStream));
		
		// This will create an file with name 2018-01-31T13:09:00_Lead.csv
		StringBuilder filePath = new StringBuilder(exportfilepath)
						.append(fileName);
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath.toString())))){
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.write("\n");
			}
		}
		reader.close();
	}

	private MarketoResponse pollingStatus(String batchId) {
		log.info("Polling Job status for batch {}", batchId);
		String url = buildUrl(MarketoConstants.POLL_STATUS_LEAD_IMPORT_URL, batchId);
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
								 	.until(resp -> "Complete".equals(resp.getResult().get(0).getStatus())
								 				|| "Failed".equals(resp.getResult().get(0).getStatus()))
								 	.execute();
		} catch (InterruptedException e) {
			log.error(e.getMessage(),e);
		}
		
		return response;
		
	}

	private MarketoResponse createJob(Resource resource) throws MarketoException {
		log.info("Inside createJob");
		MarketoResponse resp = marketoClient.uploadLeadData(MarketoConstants.CREATE_LEAD_IMPORT_JOB_URL,resource);
		log.info("Response for create lead import job : {}",resp);
		return resp;
	}
	
	/**
	 * @param url
	 * @param exportId
	 * @return
	 */
	private String buildUrl(String url,String batchId) {
		Map<String, String> pathParam = new HashMap<>();
		pathParam.put("id", batchId);
		UriComponents uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(pathParam);
		return uri.toString();
	}
}
