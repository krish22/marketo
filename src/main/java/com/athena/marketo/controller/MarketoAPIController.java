package com.athena.marketo.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.athena.marketo.exception.MarketoException;
import com.athena.marketo.scheduler.BaseScheduler;
import com.athena.marketo.scheduler.LeadImportScheduler;

@RestController
@RequestMapping("/api/v1/marketo")
public class MarketoAPIController {

	private static final Logger log = LoggerFactory.getLogger(MarketoAPIController.class);
	
	@Autowired
	private LeadImportScheduler leadImportScheduler;
	
	@Autowired
	@Qualifier("LeadExportScheduler")
	private BaseScheduler leadExtractScheduler;
	
	
	@Autowired
	@Qualifier("ActivityExportScheduler")
	private BaseScheduler activityExtractScheduler;
	
	@PostMapping("/uploadLeadData")
	public ResponseEntity<String> uploadLeadData(@RequestParam("file") MultipartFile file) throws MarketoException, IOException{
		log.info("Start uploadLeadData");
		leadImportScheduler.run(file);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Upload Lead data submitted succefully !!!.");	
	}
	
	@GetMapping
	public String healthCheck() {
		log.info("Start {}","healthCheck");
		return "OK";
	}
	
	@GetMapping("/getLeadData")
	public ResponseEntity<String> getLeadData(@RequestParam("startAt") String startAt, @RequestParam("endAt") String endAt) throws MarketoException{
		log.info("Start getLeadData");
		leadExtractScheduler.run(startAt, endAt);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Lead Extract Submitted Successfully !!! Please check your folder after sometime.");	 
	}
	
	
	@GetMapping("/getActivityData")
	public ResponseEntity<String> getActivityData(@RequestParam("startAt") String startAt, @RequestParam("endAt") String endAt) throws MarketoException{
		log.info("Start getLeadData");
		activityExtractScheduler.run(startAt, endAt);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Activity Extract Submitted Successfully !!! Please check your folder after sometime.");	 
	}

}
