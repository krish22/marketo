package com.athena.marketo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.athena.marketo.utils.MarketoConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class AuthService extends BaseService{

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
	
	public ObjectNode getIdentity() {
		log.info("start getIdentity");
		HttpHeaders headers = new HttpHeaders();
	    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
	    
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + MarketoConstants.IDENTITY_URL)
				.queryParam(MarketoConstants.GRANT_TYPE, MarketoConstants.CLIENT_CREDENTIALS)
				.queryParam(MarketoConstants.CLIENT_ID, clientId)
				.queryParam(MarketoConstants.CLIENT_SECRET, secretId);
		
		ResponseEntity<ObjectNode> response = rest.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, ObjectNode.class);
		log.info("End getIdentity : {}",response.getBody());
		return response.getBody();
	}
}
