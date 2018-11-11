package com.athena.marketo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/v1/marketo")
public class MarketoAPIController {

	private static final Logger log = LoggerFactory.getLogger(MarketoAPIController.class);
	
	@PostMapping("/uploadLeadData")
	public ResponseEntity<ObjectNode> uploadLeadData(@RequestParam("file") MultipartFile file){
		log.info("Start uploadLeadData");
		return null;	
	}
	
	@GetMapping
	public String healthCheck() {
		log.info("Start {}","healthCheck");
		return "OK";
	}

}
