package com.athena.marketo.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.athena.marketo.utils.MarketoConstants;

@Component
public class LeadExportScheduler extends BaseScheduler{

	private static final Logger log = LoggerFactory.getLogger(LeadExportScheduler.class);
	
	public LeadExportScheduler() {
		this.setCreateJobUrl(MarketoConstants.CREATE_LEAD_EXPORT_JOB_URL);
		this.setStartJobUrl(MarketoConstants.START_LEAD_EXPORT_JOB_URL);
		this.setPollStatusJobUrl(MarketoConstants.POLL_STATUS_LEAD_EXPORT_JOB_URL);
		this.setRetrieveDataUrl(MarketoConstants.RETRIEVE_LEAD_EXPROT_JOB_URL);
		this.setCancelJobUrl(MarketoConstants.CANCEL_LEAD_EXPORT_JOB_URL);
		this.setAction("LeadExportScheduler");
	}
	
	@Scheduled(fixedRate=1000*30)
	@Override
	public void run() {
		log.info("Running LeadExportScheduler");
		
		//Step 1 : Create a job 
		String exportId = createExportJob(populateRequest());
		
		boolean success = processJob(exportId);
		
		log.info("ExpordId : {}",exportId);
		
	}

	/**
	 * @return
	 */
	@Override
	protected Map<String, Object> populateRequest() {
		Map<String,Object> requestMap = new HashMap<>();
		
		List<String> fieldsNames = new ArrayList<>();
		fieldsNames.add("firstName");
		fieldsNames.add("lastName");
		fieldsNames.add("id");
		fieldsNames.add("email");
		
		requestMap.put("fields", fieldsNames);
		requestMap.put("format", "csv");
		
		Map<String,String> columnsHeaderName = new HashMap<>();
		columnsHeaderName.put("firstName", "First Name");
		columnsHeaderName.put("lastName", "Last Name");
		columnsHeaderName.put("id", "Lead Id");
		columnsHeaderName.put("email", "Email address");
		
		requestMap.put("columnHeaderNames", columnsHeaderName);
		
		Map<String,Object> filter = new HashMap<>();
		
		Map<String,String> createdAtFilter = new HashMap<>();
		createdAtFilter.put("startAt", "2017-01-01T00:00:00Z");
		createdAtFilter.put("endAt", "2017-01-31T00:00:00Z");
		
		filter.put("createdAt", createdAtFilter);
		
		requestMap.put("filter", filter);
		return requestMap;
	}
	
}
