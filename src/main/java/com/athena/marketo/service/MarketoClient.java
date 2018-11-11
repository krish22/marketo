package com.athena.marketo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.athena.marketo.config.RestTemplateLoggingInterceptor;
import com.athena.marketo.utils.MarketoConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class MarketoClient {

	private static final Logger log = LoggerFactory.getLogger(MarketoClient.class);
	
	private final RestTemplate rest;
	
	@Value("${developer.marketo.baseUrl}")
    private String baseUrl;
	
	@Value("${developer.marketo.clientId}")
    private String clientId;

	@Value("${developer.marketo.secretId}")
    private String secretId;
	
	public MarketoClient() {
		rest = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        rest.setInterceptors(Collections.singletonList(new RestTemplateLoggingInterceptor()));
	}
	
	@Cacheable(value="identity")
	public JsonNode getIdentityToken() {
		log.info("Calling getIdentityToken and caching");
		return getIdentity();
	}
	
	@CacheEvict(value="identity",allEntries = true)
	public void removeIdentityFromCache() { log.info("Identity token is expired and removing it from cache");}
	
	private ObjectNode post(String url,String requestBody) {
		log.info("start post");
		HttpHeaders headers = getHttpHeaders();
	    HttpEntity<String> requestEntity =null;
	    if(null != requestBody) {
	    	requestEntity = new HttpEntity<String>(requestBody, headers);
	    }else {
	    	requestEntity = new HttpEntity<String>(headers);
	    }
		
	    ResponseEntity<ObjectNode> response = rest.exchange(url, HttpMethod.POST, requestEntity, ObjectNode.class); 
	    log.info("End post : {}",response.getBody()); 
		return response.getBody();
		
	}

	private ObjectNode get(String url) {
		log.info("start get");
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<ObjectNode> response = rest.exchange(url, HttpMethod.GET, requestEntity, ObjectNode.class);
		log.info("End getIdentity : {}",response.getBody());
		return response.getBody();
	}
	
	private InputStream getFile(String url) throws IOException {
		log.info("start getFile");
		HttpHeaders headers = getHttpHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<Resource> response = rest.exchange(url, HttpMethod.GET, requestEntity, Resource.class);
		log.info("End getIdentity : {}",response.getBody());
		return response.getBody().getInputStream();
	}
	private ObjectNode getIdentity() {
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
	
	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		JsonNode resp = getIdentityToken();
		
		StringBuilder token = new StringBuilder(resp.get("token_type").textValue())
				.append(" ")
				.append(resp.get("access_token").textValue());
		log.info("header token , {} ",token);
		headers.add("Authorization",token.toString());
		return headers;
	}
	
	public ObjectNode createJob(String url,Map<String, Object> requestBody) {
		log.info("start createJob");
		return post(url,String.valueOf(requestBody));
	}

	public ObjectNode startJob(String url) {
		log.info("start createJob");
		return post(url,null);
	}
	
	public ObjectNode pollingJobStatus(String url) {
		log.info("start pollingJobStatus");
		return get(url);
	}

	public InputStream retrieveExportData(String url) throws IOException {
		return getFile(url);
	}
}
