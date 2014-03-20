package com.pearson.lt.mobileplatform.android.push.models;

import java.util.HashMap;
import java.util.Map;

import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonAnyGetter;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonAnySetter;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.JsonNode;

public class PayloadResponse {

	protected Map<String, JsonNode> properties = new HashMap<String, JsonNode>();
	
	public PayloadResponse() {
	}
	
    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperty(String name, JsonNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }

}
