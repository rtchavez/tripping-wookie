package com.pearson.lt.mobileplatform.android.push.gcm;

import android.net.Uri;
import android.os.Build;

import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonInclude;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.pearson.lt.mobileplatform.appservices.android.sdk.apm.android.JacksonMarshallingService;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.DataClient;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.callbacks.ApiResponseCallback;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.callbacks.ClientAsyncTask;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.entities.Device;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.response.ApiResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.utils.ObjectUtils.isEmpty;
import static com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.utils.UrlUtils.addQueryParams;
import static com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.utils.UrlUtils.encodeParams;
import static com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.utils.UrlUtils.path;

/**
 * Created by Ryan Chavez on 9/11/13.
 */
public class PushDataClient extends DataClient {

    private static final String API_KEY = "api_key";
    private String apiKey;
    private String authToken;

    public PushDataClient(String organizationId, String applicationId, String baseURL) {
        super(organizationId, applicationId, baseURL);
    }

    @Override
    public Device registerDevice(UUID deviceId, Map<String, Object> properties) {
        assertValidApplicationId();
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        properties.put("refreshed", System.currentTimeMillis());

        // add device meta-data
        properties.put("type", "device");
        properties.put("deviceModel", Build.MODEL);
        properties.put("devicePlatform", "android");
        properties.put("deviceOSVersion", Build.VERSION.RELEASE);

        ApiResponse response = apiRequest(HTTP_METHOD_PUT, null, properties,
                "appservices", getOrganizationId(), getApplicationId(), "devices", deviceId.toString());
        return response.getFirstEntity(Device.class);
    }

    public void connectUserToDeviceAsync(final UUID userId, final UUID deviceId, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return connectUserToDevice(userId, deviceId);
            }
        }).execute();
    }

    public void connectUserToDeviceAsync(final String userId, final String deviceId, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return connectUserToDevice(userId, deviceId);
            }
        }).execute();
    }

    public ApiResponse connectUserToDevice(UUID userId, UUID deviceId) {
        return connectUserToDevice(userId.toString(), deviceId.toString());
    }

    public ApiResponse connectUserToDevice(String userId, String deviceId) {
        return connectEntities("users", userId, "devices", deviceId);
    }

    @Override
    public ApiResponse connectEntities(String connectingEntityType, String connectingEntityId, String connectionType, String connectedEntityId) {
        return apiRequest(HTTP_METHOD_POST, null, null, "appservices", getOrganizationId(), getApplicationId(),
                connectingEntityType, connectingEntityId,
                connectionType, connectedEntityId);
    }

    @Override
    public ApiResponse createEntity(Map<String, Object> properties) {
        assertValidApplicationId();
        if (isEmpty(properties.get("type"))) {
            throw new IllegalArgumentException("Missing entity type");
        }
        ApiResponse response = apiRequest(HTTP_METHOD_POST, null, properties, "appservices",
                getOrganizationId(), getApplicationId(), properties.get("type").toString());
        return response;
    }

    private ApiResponse createUserEntity(String username, Map<String, Object> properties) {
        assertValidApplicationId();
        if (isEmpty(properties.get("type"))) {
            throw new IllegalArgumentException("Missing entity type");
        }
        ApiResponse response = apiRequest(HTTP_METHOD_PUT, null, properties, "appservices",
                getOrganizationId(), getApplicationId(), properties.get("type").toString(), username);
        return response;
    }

    public void disconnectUserFromDeviceAsync(final UUID userId, final UUID deviceId, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return disconnectUserFromDevice(userId, deviceId);
            }
        }).execute();
    }

    public void disconnectUserFromDeviceAsync(final String userId, final String deviceId, final ApiResponseCallback callback) {
        (new ClientAsyncTask<ApiResponse>(callback) {
            @Override
            public ApiResponse doTask() {
                return disconnectUserFromDevice(userId, deviceId);
            }
        }).execute();
    }

    public ApiResponse disconnectUserFromDevice(UUID userId, UUID deviceId) {
        return disconnectUserFromDevice(userId.toString(), deviceId.toString());
    }

    public ApiResponse disconnectUserFromDevice(String userId, String deviceId) {
        return disconnectEntities("users", userId, "devices", deviceId);
    }

    @Override
    public ApiResponse disconnectEntities(String connectingEntityType, String connectingEntityId, String connectionType, String connectedEntityId) {
        return apiRequest(HTTP_METHOD_DELETE, null, null, "appservices", getOrganizationId(), getApplicationId(),
                connectingEntityType, connectingEntityId,
                connectionType, connectedEntityId);
    }

    public ApiResponse createUser(String username) {
        return createUser(username, null, null, null);
    }

    @Override
    public ApiResponse createUser(String username, String name, String email, String password) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "user");
        if (username != null) {
            properties.put("username", username);
        }
        if (name != null) {
            properties.put("name", name);
        }
        if (email != null) {
            properties.put("email", email);
        }
        if (password != null) {
            properties.put("password", password);
        }
        return createUserEntity(username, properties);
    }

    public void createUserAsync(String username, ApiResponseCallback callback) {
        createUserAsync(username, null, null, null, callback);
    }

    public ApiResponse queryUserByUsername(String username) {
        return getEntities("users", String.format("username='%s'", username));
    }

    public ApiResponse getEntities(String type, String queryString) {
        Map<String, Object> params = null;

        if (queryString.length() > 0) {
            params = new HashMap<String, Object>();
            params.put("ql", queryString);
        }

        return apiRequest(HTTP_METHOD_GET, // method
                params, // params
                null, // data
                "appservices",
                getOrganizationId(),
                getApplicationId(),
                type);
    }

    @Override
    public ApiResponse doHttpRequest(String httpMethod, Map<String, Object> params, Object data, String... segments) {
        ApiResponse response = null;
        OutputStream out = null;
        InputStream in = null;
        HttpURLConnection conn = null;

        final String apiUrl = getApiUrl();
        String urlAsString = path(apiUrl, segments);

        try {
            String contentType = "application/json";
            if (httpMethod.equals(HTTP_METHOD_POST) && isEmpty(data) && !isEmpty(params)) {
                params.remove(API_KEY);
                data = encodeParams(params);
                contentType = "application/x-www-form-urlencoded";
            } else {
                urlAsString = addQueryParams(urlAsString, params);
            }

            Uri uri = Uri.parse(urlAsString)
                    .buildUpon()
                    .appendQueryParameter(API_KEY, getApiKey())
                    .build();
            logTrace("Invoking " + httpMethod + " to '" + uri.toString() + "'");

            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(httpMethod);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Connection", "Close");
            conn.setUseCaches(false);

            if ((authToken != null) && (authToken.length() > 0)) {
                String authStr = "Bearer " + authToken;
                conn.setRequestProperty("X-Authorization", authStr);
            }
            final String accessToken = getAccessToken();
            if ((accessToken != null) && (accessToken.length() > 0)) {
                String authStr = "Bearer " + accessToken;
                conn.setRequestProperty("Authorization", authStr);
            }

            conn.setDoInput(true);

            if (httpMethod.equals(HTTP_METHOD_POST) || httpMethod.equals(HTTP_METHOD_PUT)) {
                if (isEmpty(data)) {
                    data = JsonNodeFactory.instance.objectNode();
                }

                String dataAsString = null;

                if ((data != null) && (!(data instanceof String))) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    dataAsString = objectMapper.writeValueAsString(data);
                } else {
                    dataAsString = (String) data;
                }

                logTrace("Posting/putting data: '" + dataAsString + "'");

                byte[] dataAsBytes = dataAsString.getBytes();

                conn.setRequestProperty("Content-Length", Integer.toString(dataAsBytes.length));
                conn.setDoOutput(true);

                out = conn.getOutputStream();
                out.write(dataAsBytes);
                out.flush();
                out.close();
                out = null;
            }

            Map<String, List<String>> headerMap = conn.getHeaderFields();
            if (headerMap != null) {
                logTrace("Headers:");
                for (String key : headerMap.keySet()) {
                    for (String value : headerMap.get(key)) {
                        logTrace(key + " : " + value);
                    }
                }
            }

            in = conn.getInputStream();
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }


                String responseAsString = sb.toString();
                //logTrace("response from server: '" + responseAsString + "'");

                JacksonMarshallingService marshallingService = new JacksonMarshallingService();
                response = (ApiResponse) marshallingService.demarshall(responseAsString, ApiResponse.class);
                if (response != null) {
                    response.setRawResponse(responseAsString);
                }

                response.setDataClient(this);
            } else {
                response = null;
                logTrace("no response body from server");
            }
        } catch (Exception e) {
            logError("Error " + httpMethod + " to '" + urlAsString + "'");
            if (e != null) {
                e.printStackTrace();
                logError(e.getLocalizedMessage());
            }
            response = null;
        } catch (Throwable t) {
            logError("Error " + httpMethod + " to '" + urlAsString + "'");
            if (t != null) {
                t.printStackTrace();
                logError(t.getLocalizedMessage());
            }
            response = null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

                if (conn != null) {
                    final int responseCode = conn.getResponseCode();
                    logTrace("responseCode from server = " + responseCode + " - " + conn.getResponseMessage());
                    conn.disconnect();
                }
            } catch (Exception ignored) {
            }
        }

        return response;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
