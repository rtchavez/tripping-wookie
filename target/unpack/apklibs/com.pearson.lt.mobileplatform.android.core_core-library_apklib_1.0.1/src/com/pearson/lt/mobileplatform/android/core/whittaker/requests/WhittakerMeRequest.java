package com.pearson.lt.mobileplatform.android.core.whittaker.requests;

import android.net.Uri;

import com.pearson.lt.mobileplatform.android.core.json.JsonUtils;
import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.databind.JsonNode;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.GenericUrl;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpHeaders;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpRequest;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpResponse;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.json.jackson2.JacksonFactory;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

/**
 * Created by chavry on 10/24/13.
 */
public class WhittakerMeRequest extends GoogleHttpClientSpiceRequest<String> {

    private static final String WHITTAKER_ROOT = "http://ecollege-test.apigee.net/registrar/me";
    private final Uri root;
    private final String token;

    public WhittakerMeRequest(final String token) {
        super(String.class);

        root = Uri.parse(WHITTAKER_ROOT).buildUpon().build();
        this.token = token;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Bearer " + token);
        headers.set("Connection", "Close");

        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(root.toString()))
                .setHeaders(headers)
                .setParser(new JacksonFactory().createJsonObjectParser());

        request.getThrowExceptionOnExecuteError();

        HttpResponse response = request.execute();
        String json = response.parseAsString();

        JsonNode node = JsonUtils.createJsonNodeFromJson(json);
        String userId = node.path("data").path("id").textValue();

        return userId;
    }

}
