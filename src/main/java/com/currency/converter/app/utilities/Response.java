package com.currency.converter.app.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
    private static final Logger log = LoggerFactory.getLogger(Response.class.getName());
    public static String print(String response, String messageCodes) {
        JSONObject JSONresponse = new JSONObject();

        JSONresponse.put("response", response);
        JSONresponse.put("message", messageCodes);

        return JSONresponse.toString();
    }
    public static String print( JSONObject result) {
        return new JSONObject().put("response", result).toString();
    }

}
