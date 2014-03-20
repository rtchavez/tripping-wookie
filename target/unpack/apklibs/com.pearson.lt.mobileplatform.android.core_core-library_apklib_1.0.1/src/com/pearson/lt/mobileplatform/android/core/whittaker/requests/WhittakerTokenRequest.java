package com.pearson.lt.mobileplatform.android.core.whittaker.requests;

import android.net.Uri;

import com.pearson.lt.mobileplatform.android.core.json.JsonUtils;
import com.pearson.lt.mobileplatform.android.core.logging.Logger;
import com.pearson.lt.mobileplatform.android.core.utils.Maps;
import com.pearson.lt.mobileplatform.android.core.whittaker.requests.models.WhittakerTokenResponse;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.GenericUrl;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpHeaders;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpRequest;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpResponse;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.UrlEncodedContent;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.json.jackson2.JacksonFactory;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.util.Map;

/**
 * Created by chavry on 10/24/13.
 */
public class WhittakerTokenRequest extends GoogleHttpClientSpiceRequest<WhittakerTokenResponse> {

    private final static String API_KEY = "fYObWNEnlWYJYw1FiYWupt1KgymBRnBA";
    private final static String WHITTAKER_ROOT = "http://ecollege-test.apigee.net/oauth/token";
    private final static String GRANT_TYPE = "authorization_code";
    private final Uri root;
    private final String code;
    private final String base64;

    public WhittakerTokenRequest(final String code, final String base64) {
        super(WhittakerTokenResponse.class);

        this.root = Uri.parse(WHITTAKER_ROOT)
                .buildUpon()
                .appendQueryParameter("apikey", API_KEY).build();
        this.code = code;
        this.base64 = base64;
    }

    @Override
    public WhittakerTokenResponse loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        // the header "Content-Type: application/x-www-form-urlencoded"
        // does not need to be set here as it is done via UrlEncodedContent
        headers.setAuthorization("Basic " + base64);
        headers.set("Connection", "Close");

        Map<String, String> data = Maps.newLinkedHashMap();
        data.put("grant_type", GRANT_TYPE);
        data.put("code", code);
        UrlEncodedContent content = new UrlEncodedContent(data);

        HttpRequest request = getHttpRequestFactory()
                .buildPostRequest(new GenericUrl(root.toString()), content)
                .setHeaders(headers)
                .setParser(new JacksonFactory().createJsonObjectParser());

        HttpResponse response = request.execute();
        String json = response.parseAsString();

        WhittakerTokenResponse result = JsonUtils.parse(json, WhittakerTokenResponse.class);
        return result;
    }

}
