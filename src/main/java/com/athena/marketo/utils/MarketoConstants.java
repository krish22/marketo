package com.athena.marketo.utils;

public interface MarketoConstants {
	
	String IDENTITY_URL = "/identity/oauth/token";

	String CLIENT_SECRET = "client_secret";

	String CLIENT_ID = "client_id";

	String GRANT_TYPE = "grant_type";
	
	String CLIENT_CREDENTIALS = "client_credentials";
	
	String CREATE_LEAD_EXPORT_JOB_URL = "/bulk/v1/leads/export/create.json";
	String START_LEAD_EXPORT_JOB_URL = "/bulk/v1/leads/export/{exportId}/enqueue.json";
	String POLL_STATUS_LEAD_EXPORT_JOB_URL = "/bulk/v1/leads/export/{exportId}/status.json";
	String RETRIEVE_LEAD_EXPROT_JOB_URL = "/bulk/v1/leads/export/{exportId}/file.json";
	String CANCEL_LEAD_EXPORT_JOB_URL = "/bulk/v1/leads/export/{exportId}/cancel.json";
	
	String CREATE_ACTIVITY_EXPORT_JOB_URL = "/bulk/v1/activities/export/create.json";
	String START_ACTIVITY_EXPORT_JOB_URL = "/bulk/v1/activities/export/{exportId}/enqueue.json";
	String POLL_STATUS_ACTIVITY_EXPORT_JOB_URL = "/bulk/v1/activities/export/{exportId}/status.json";
	String RETRIEVE_ACTIVITY_EXPROT_JOB_URL = "/bulk/v1/activities/export/{exportId}/file.json";
	String CANCEL_ACTIVITY_EXPORT_JOB_URL = "/bulk/v1/activities/export/{exportId}/cancel.json";
	
	String CREATE_LEAD_IMPORT_JOB_URL = "/bulk/v1/leads.json?format=csv";
	String POLL_STATUS_LEAD_IMPORT_URL = "/bulk/v1/leads/batch/{id}.json";
	String FAILURE_LEAD_IMPORT_JOB_URL = "/bulk/v1/leads/batch/{id}/failures.json";
	String WARNING_LEAD_IMPORT_JOB_URL = "/bulk/v1/leads/batch/{id}/warnings.json";
	
	
	
}
