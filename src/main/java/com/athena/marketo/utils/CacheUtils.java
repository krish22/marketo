package com.athena.marketo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.athena.marketo.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class CacheUtils {

	private static final Logger log = LoggerFactory.getLogger(CacheUtils.class);
	
	@Autowired
	private AuthService authService;
	
	@Cacheable(value="identity")
	public JsonNode getIdentityToken() {
		log.info("Calling getIdentityToken and caching");
		return authService.getIdentity();
	}
	
	@CacheEvict(value="identity",allEntries = true)
	public void removeIdentityFromCache() { log.info("Identity token is expired and removing it from cache");}
	
}
