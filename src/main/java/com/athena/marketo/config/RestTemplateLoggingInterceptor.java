package com.athena.marketo.config;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;


public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger log = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);
	
	private int requestNumber;
	
    /* (non-Javadoc)
     * @see org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.springframework.http.HttpRequest, byte[], org.springframework.http.client.ClientHttpRequestExecution)
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        int count = requestNumber;
        long startTime = System.currentTimeMillis();
    	logRequest(count + " >>>> ",request, body);
    	requestNumber++;  
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(count + " <<<< ",response,startTime);
        return response;
    }
 
    /**
     * Log request.
     * @param request the request
     * @param body the body
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void logRequest(String prefix, HttpRequest request, byte[] body) throws IOException {
            log.info("Calling external API begins");
            log.info("{} URI : {} {}", prefix,request.getMethod(),request.getURI());
            log.debug("{} Headers     : {}", prefix,request.getHeaders());
            log.debug("{} Request body: {}", prefix,new String(body, "UTF-8"));
    }
 
    /**
     * Log response.
     * @param response the response
     * @param startTime 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void logResponse(String prefix, ClientHttpResponse response, long startTime) throws IOException {
            log.info("{} Status : {} {}",prefix, response.getStatusCode(),response.getStatusText());
            log.debug("{} Headers      : {}", prefix,response.getHeaders());
            log.debug("{} Response body: {}", prefix, StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
            log.info("{} took {} ms ",prefix,System.currentTimeMillis()-startTime);
            
    }
}
