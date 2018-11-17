package com.athena.marketo.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.athena.marketo.exception.MarketoException;
import com.athena.marketo.utils.JsonUtils;
import com.athena.marketo.utils.MarketoConstants;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class LeadExportScheduler extends BaseScheduler{

	private static final Logger log = LoggerFactory.getLogger(LeadExportScheduler.class);
	
	public LeadExportScheduler() {
		this.setCreateJobUrl(MarketoConstants.CREATE_LEAD_EXPORT_JOB_URL);
		this.setStartJobUrl(MarketoConstants.START_LEAD_EXPORT_JOB_URL);
		this.setPollStatusJobUrl(MarketoConstants.POLL_STATUS_LEAD_EXPORT_JOB_URL);
		this.setRetrieveDataUrl(MarketoConstants.RETRIEVE_LEAD_EXPROT_JOB_URL);
		this.setCancelJobUrl(MarketoConstants.CANCEL_LEAD_EXPORT_JOB_URL);
		this.setAction("Lead");
	}
	
	
	/* (non-Javadoc)
	 * This job will run at everyday at 12 AM mid night
	 * @see com.athena.marketo.scheduler.BaseScheduler#run()
	 */
	//@Scheduled(cron = "0 0 * * * ?")
	@Async
	@Scheduled(fixedRate = 1000*60*60)
	@Override
	public void run() throws MarketoException {
		log.info("Running LeadExportScheduler");
		
		//Step 1 : Create a job 
		String exportId = createExportJob(populateRequest());
		
		boolean success = processJob(exportId);
		
		log.info("ExpordId : {} is completed successfully and the file is stored in the disk",exportId);
		
	}

	/**
	 * @return
	 */
	@Override
	protected ObjectNode populateRequest() {
		ObjectNode requestMap = JsonUtils.objectNode();
		
		ArrayNode fieldsNames = JsonUtils.getObjectMapper().createArrayNode();
		fieldsNames.add("firstName");
		fieldsNames.add("lastName");
		fieldsNames.add("id");
		fieldsNames.add("email");
		
		requestMap.putArray("fields").addAll(fieldsNames);
		requestMap.put("format", "csv");
		
		ObjectNode columnsHeaderName = JsonUtils.objectNode();
		columnsHeaderName.put("firstName", "First Name");
		columnsHeaderName.put("lastName", "Last Name");
		columnsHeaderName.put("id", "Lead Id");
		columnsHeaderName.put("email", "Email address");
		
		requestMap.set("columnHeaderNames",columnsHeaderName);
		
		ObjectNode filter = JsonUtils.objectNode();
		
		filter.set("createdAt", createdAtFilter());
		
		requestMap.set("filter", filter);
		return requestMap;
	}
	
}
