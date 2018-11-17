package com.athena.marketo.utils;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtils {

	 private JsonUtils() { }

	    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	    static {
	        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'"));
	    }

	    public static ObjectMapper getObjectMapper() {
	        return OBJECT_MAPPER;
	    }

	    public static ObjectNode objectNode() {
	        return OBJECT_MAPPER.createObjectNode();
	    }

	    public static ArrayNode arrayNode() {
	        return OBJECT_MAPPER.createArrayNode();
	    }
	    
}
