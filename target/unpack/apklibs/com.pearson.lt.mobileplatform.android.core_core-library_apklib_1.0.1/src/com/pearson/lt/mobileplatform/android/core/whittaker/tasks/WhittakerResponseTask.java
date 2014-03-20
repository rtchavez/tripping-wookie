package com.pearson.lt.mobileplatform.android.core.whittaker.tasks;

import android.util.Base64;

import com.pearson.lt.mobileplatform.android.core.tasks.Task;
import com.pearson.lt.mobileplatform.android.core.tasks.TaskCallback;
import com.pearson.lt.mobileplatform.android.core.utils.ServerUtilities;
import com.pearson.lt.mobileplatform.android.core.whittaker.models.WhittakerResponse;
import com.pearson.lt.mobileplatform.android.core.whittaker.requests.WhittakerAuthenticationRequest;
import com.pearson.lt.mobileplatform.android.core.whittaker.requests.WhittakerMeRequest;
import com.pearson.lt.mobileplatform.android.core.whittaker.requests.WhittakerTokenRequest;
import com.pearson.lt.mobileplatform.android.core.whittaker.requests.models.WhittakerTokenResponse;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpRequestFactory;

/**
 * Created by chavry on 10/25/13.
 */
public class WhittakerResponseTask extends Task<WhittakerResponse> {

    private final String email;
    private final String password;
    private final String clientId;
    private final String shared;

    public WhittakerResponseTask(final String email, final String password,
                                 final String clientId, final String shared,
                                 final TaskCallback<WhittakerResponse> callback) {
        super(callback);
        this.email = email;
        this.password = password;
        this.clientId = clientId;
        this.shared = shared;
    }

    @Override
    public WhittakerResponse performTask() throws Exception {
        HttpRequestFactory httpRequestFactory = ServerUtilities.createNetHttpRequestFactory();

        WhittakerAuthenticationRequest whittakerAuthenticationRequest
                = new WhittakerAuthenticationRequest(clientId, email, password);
        whittakerAuthenticationRequest.setHttpRequestFactory(httpRequestFactory);
        String code = whittakerAuthenticationRequest.loadDataFromNetwork();

        final byte[] input = (clientId + ":" + shared).getBytes("UTF-8");
        final String base64 = Base64.encodeToString(input, Base64.DEFAULT | Base64.NO_WRAP);
        WhittakerTokenRequest whittakerTokenRequest = new WhittakerTokenRequest(code, base64);

        whittakerTokenRequest.setHttpRequestFactory(httpRequestFactory);
        WhittakerTokenResponse whittakerTokenResponse = whittakerTokenRequest.loadDataFromNetwork();

        final String token = whittakerTokenResponse.getAccessToken();

        WhittakerMeRequest whittakerMeRequest = new WhittakerMeRequest(token);
        whittakerMeRequest.setHttpRequestFactory(httpRequestFactory);
        final String userId = whittakerMeRequest.loadDataFromNetwork();

        WhittakerResponse response = new WhittakerResponse(userId, token);
        return response;
    }

}
