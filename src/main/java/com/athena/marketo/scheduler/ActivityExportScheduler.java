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
public class ActivityExportScheduler extends BaseScheduler{

	private static final Logger log = LoggerFactory.getLogger(ActivityExportScheduler.class);
	
	public ActivityExportScheduler() {
		this.setCreateJobUrl(MarketoConstants.CREATE_ACTIVITY_EXPORT_JOB_URL);
		this.setStartJobUrl(MarketoConstants.START_ACTIVITY_EXPORT_JOB_URL);
		this.setPollStatusJobUrl(MarketoConstants.POLL_STATUS_ACTIVITY_EXPORT_JOB_URL);
		this.setRetrieveDataUrl(MarketoConstants.RETRIEVE_ACTIVITY_EXPROT_JOB_URL);
		this.setCancelJobUrl(MarketoConstants.CANCEL_ACTIVITY_EXPORT_JOB_URL);
		this.setAction("Activity");
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
		log.info("Running ActivityExportScheduler");
		
		//Step 1 : Create a job 
		String exportId = createExportJob(populateRequest());
		
		boolean success = processJob(exportId);
		
		log.info("ExpordId : {} is completed successfully and the file is stored in the disk",exportId);
				
	}

	@Override
	protected ObjectNode populateRequest() {
		ObjectNode requestMap = JsonUtils.objectNode();
		
		ArrayNode activityTypeIds = JsonUtils.arrayNode();
		activityTypeIds.add(1);
		activityTypeIds.add(32);
		activityTypeIds.add(12);
		activityTypeIds.add(34);
		
		requestMap.putArray("activityTypeIds").addAll(activityTypeIds);
		requestMap.put("format", "csv");
		
		ObjectNode filter = JsonUtils.objectNode();
		
		
		/*Map<String,String> createdAtFilter = new HashMap<>();
		createdAtFilter.put("startAt", "2017-07-01T23:59:59-00:00");
		createdAtFilter.put("endAt", "2017-07-31T23:59:59-00:00");*/
		
		filter.set("createdAt", createdAtFilter());
		
		requestMap.set("filter", filter);
		
		return requestMap;
	}

}

