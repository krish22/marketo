package com.athena.marketo.service;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.athena.marketo.domain.MarketoError;
import com.athena.marketo.domain.MarketoResponse;
import com.athena.marketo.exception.MarketoException;
import com.athena.marketo.utils.CacheUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class MarketoClient extends BaseService{

	private static final Logger log = LoggerFactory.getLogger(MarketoClient.class);
	
	@Autowired
	private CacheUtils cacheUtils;
	
	private MarketoResponse post(String url,String requestBody) throws MarketoException {
		log.info("Inside post method");
		
		HttpHeaders headers = getHttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
	    HttpEntity<String> requestEntity =null;
	    if(null != requestBody) {
	    	requestEntity = new HttpEntity<String>(requestBody, headers);
	    }else {
	    	requestEntity = new HttpEntity<String>(headers);
	    }
		
	    ResponseEntity<MarketoResponse> response = rest.exchange(url, HttpMethod.POST, requestEntity, MarketoResponse.class); 
	    
	    MarketoResponse resp = response.getBody();
	    
	    //This check will ensure the following
	    //1. if the request is not succeed then it will throw the MarketoException
	    //2.  If the request is not succed due to Authorization token invalid or expired , then it will generate a new token and re
	    //	  re-try the same requst
	    if(!resp.isSuccess() && !handleError(resp)) {
	    	return this.post(url, requestBody);
	    }
	    
	    log.info("End post : {}",resp);
	    
		return resp;
	}

	private boolean handleError(MarketoResponse resp) throws MarketoException {
		if(null!=resp.getErrors() && !resp.getErrors().isEmpty()) {
			MarketoError error = resp.getErrors().get(0);
			if("601".equals(error.getCode()) || "602".equals(error.getCode())) {
				log.info("Authorization token is invalid or expired .. Hence refresing the cache");
				cacheUtils.removeIdentityFromCache();
				return false;
			}
			log.error("Error in request : {}",error);
			throw new MarketoException(error.getCode(), error.getMessage());
		}
		return true;
	}
	
	private MarketoResponse get(String url) throws MarketoException {
		log.info("start get");
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<MarketoResponse> response = rest.exchange(url, HttpMethod.GET, requestEntity, MarketoResponse.class); 
		MarketoResponse resp = response.getBody();
		log.info("End getIdentity : {}",resp);
		
		 //This check will ensure the following
	    //1. if the request is not succeed then it will throw the MarketoException
	    //2.  If the request is not succed due to Authorization token invalid or expired , then it will generate a new token and re
	    //	  re-try the same requst
	    if(!resp.isSuccess() && !handleError(resp)) {
	    	return this.get(url);
	    }
		
		return resp;
	}
	
	private InputStream getFile(String url) throws IOException {
		log.info("start getFile");
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<Resource> response = rest.exchange(url, HttpMethod.GET, requestEntity, Resource.class);

		InputStream is = response.getBody().getInputStream();
		/*
		if(response.getStatusCodeValue() == 601 || response.getStatusCodeValue() == 602) {
	    	log.info("Authorization token is invalid or expired .. Hence refresing the cache" );
	    	this.removeIdentityFromCache();
	    	return getFile(url);
	    }*/
		
		return is;
	}
	
	
	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		JsonNode resp = cacheUtils.getIdentityToken();
		
		StringBuilder token = new StringBuilder(resp.get("token_type").textValue())
				.append(" ")
				.append(resp.get("access_token").textValue());
		log.debug("header token , {} ",token);
		headers.add("Authorization",token.toString());
		return headers;
	}
	
	public String createJob(String url,ObjectNode requestBody) throws MarketoException {
		log.info("inside createJob");
		MarketoResponse  resp = post(baseUrl+url,String.valueOf(requestBody));
		 
		return resp.getResult().get(0).getExportId();
	}

	public MarketoResponse startJob(String url) throws MarketoException {
		log.info("inside startJob");
		return post(baseUrl+url,null);
	}
	
	public MarketoResponse pollingJobStatus(String url) throws MarketoException {
		log.info("inside pollingJobStatus");
		return get(baseUrl+url);
	}

	public InputStream retrieveExportData(String url) throws IOException {
		return getFile(baseUrl+url);
	}
}
