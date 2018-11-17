package com.athena.marketo.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.athena.marketo.config.RestTemplateLoggingInterceptor;

public class BaseService {

	protected final RestTemplate rest;
	
	@Value("${developer.marketo.baseUrl}")
    protected String baseUrl;
	
	@Value("${developer.marketo.clientId}")
	protected String clientId;

	@Value("${developer.marketo.secretId}")
	protected String secretId;
	
	public BaseService() {
		rest = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        rest.setInterceptors(Collections.singletonList(new RestTemplateLoggingInterceptor()));
	}
}
