package com.pearson.lt.mobileplatform.android.core.whittaker.requests;

import android.net.Uri;

import com.pearson.lt.mobileplatform.android.core.logging.Logger;
import com.pearson.lt.mobileplatform.android.core.utils.Maps;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.GenericUrl;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpHeaders;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpRequest;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpResponse;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpResponseException;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpStatusCodes;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.UrlEncodedContent;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.util.Map;

/**
 * Created by chavry on 10/24/13.
 */
public class WhittakerAuthenticationRequest extends GoogleHttpClientSpiceRequest<String> {

    private static final String LOG_TAG = WhittakerAuthenticationRequest.class.getSimpleName();
    private static final String WHITTAKER_ROOT = "http://ecollege-test.apigee.net/oauth/authenticate/basic";
    private static final String REDIRECT_URL = "http://localhost/catch_this";
    private static final String AUTH_CODE_QUERY_PARAM = "auth_code";
    private static final String RESPONSE_TYPE = "code";
    private final String email;
    private final String password;
    private final String clientId;

    public WhittakerAuthenticationRequest(final String clientId, final String email, final String password) {
        super(String.class);

        this.clientId = clientId;
        this.email = email;
        this.password = password;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        // the header "Content-Type: application/x-www-form-urlencoded"
        // does not need to be set here as it is done via UrlEncodedContent
        headers.set("Connection", "Close");

        Map<String, String> data = Maps.newLinkedHashMap();
        data.put("response_type", RESPONSE_TYPE);
        data.put("client_id", clientId);
        data.put("email", email);
        data.put("password", password);
        data.put("redirect_url", REDIRECT_URL);
        UrlEncodedContent content = new UrlEncodedContent(data);

        HttpRequest request = getHttpRequestFactory()
                .buildPostRequest(new GenericUrl(WHITTAKER_ROOT), content)
                .setFollowRedirects(false)
                .setThrowExceptionOnExecuteError(false)
                .setHeaders(headers);

        Logger.e(LOG_TAG, "email: " + email);
        HttpResponse response = request.execute();
        final String authCode;

        final int statusCode = response.getStatusCode();

        if (statusCode == HttpStatusCodes.STATUS_CODE_FOUND) {
            Uri location = Uri.parse(response.getHeaders().getLocation());
            authCode = location.getQueryParameter(AUTH_CODE_QUERY_PARAM);
        } else {
            Logger.e(LOG_TAG, "statusCode: " + statusCode);
            Logger.e(LOG_TAG, "statusMessage: " + response.getStatusMessage());

            throw new HttpResponseException(response);
        }

        return authCode;
    }

}
