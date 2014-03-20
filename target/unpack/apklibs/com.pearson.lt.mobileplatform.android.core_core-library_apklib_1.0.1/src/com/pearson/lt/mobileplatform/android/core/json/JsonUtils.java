package com.pearson.lt.mobileplatform.android.core.json;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.core.JsonFactory;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.core.JsonGenerationException;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.core.JsonParseException;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.core.JsonParser;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.core.JsonToken;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.JsonMappingException;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.JsonNode;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.pearson.lt.mobileplatform.android.core.exception.AndroidCoreException;
import com.pearson.lt.mobileplatform.android.core.logging.Logger;

public class JsonUtils {

	private static final String LOG_TAG = JsonUtils.class.getSimpleName();
	
	static ObjectMapper mapper = new ObjectMapper();
    private static final AtomicBoolean showDebugMessages = new AtomicBoolean(false);

	public static String toJsonString(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			throw new AndroidCoreException("Unable to generate json", e);
		} catch (JsonMappingException e) {
			throw new AndroidCoreException("Unable to map json", e);
		} catch (IOException e) {
			throw new AndroidCoreException("IO error", e);
		}
	}

	public static <T> T parse(String json, Class<T> c) {
		try {
			return mapper.readValue(json, c);
		} catch (JsonParseException e) {
			throw new AndroidCoreException("Unable to parse json", e);
		} catch (JsonMappingException e) {
			throw new AndroidCoreException("Unable to map json: " + e.getPathReference(), e);
		} catch (IOException e) {
			throw new AndroidCoreException("IO error", e);
		}
	}

	public static JsonNode toJsonNode(Object obj) {
		return mapper.convertValue(obj, JsonNode.class);
	}

	public static JsonNode createJsonNodeFromJson(String content) {
		try {
			JsonFactory factory = mapper.getFactory(); 
			JsonParser jsonParser = factory.createParser(content);
			return mapper.readTree(jsonParser);
		} catch (JsonParseException e) {
			throw new AndroidCoreException("Unable to parse json.", e);
		} catch (IOException e) {
			throw new AndroidCoreException("IO error", e);
		}
	}

	public static boolean isJsonObject(String json) {
		if (json == null) {
			return false;
		}
		try {
			JsonFactory factory = mapper.getFactory(); 
			JsonParser parser = factory.createParser(json);
			
			try {
				// Sanity check: verify that we got "Json Object":
				if (parser.nextToken() == JsonToken.START_OBJECT) {
					return true;
				}
			} catch (Exception e) {
				// payload was not a JSON object
                if (showDebugMessages.get()) {
                    Logger.e(LOG_TAG, "Payload was not a JSON object.");
                }
			}
		} catch (Exception e) {
            if (showDebugMessages.get()) {
                Logger.e(LOG_TAG, e.getMessage());
            }
		}
		return false;
	}

	public static boolean isJsonArray(String json) {
		if (json == null) {
			return false;
		}
		try {
			JsonFactory factory = mapper.getFactory(); 
			JsonParser parser = factory.createParser(json);
			
			try {
				// Sanity check: verify that we got "Json Object":
				if (parser.nextToken() == JsonToken.START_ARRAY) {
					return true;
				}
			} catch (Exception e) {
				// payload was not a JSON object
                if (showDebugMessages.get()) {
                    Logger.e(LOG_TAG, "Payload was not a JSON array.");
                }
			}
		} catch (Exception e) {
            if (showDebugMessages.get()) {
                Logger.e(LOG_TAG, e.getMessage(), e);
            }
		}
		return false;
	}

	public static <T> T fromJsonNode(JsonNode json, Class<T> c) {
		try {
			JsonParser jp = json.traverse();
			return mapper.readValue(jp, c);
		} catch (JsonGenerationException e) {
			throw new AndroidCoreException("Unable to generate json", e);
		} catch (JsonMappingException e) {
			throw new AndroidCoreException("Unable to map json", e);
		} catch (IOException e) {
			throw new AndroidCoreException("IO error", e);
		}
	}

    public static void showDebugMessages(boolean show) {
        showDebugMessages.set(show);
    }
	
}
