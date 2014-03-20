package com.pearson.lt.mobileplatform.android.core.whittaker.models;

/**
 * Created by chavry on 10/24/13.
 */
public class WhittakerResponse {

    private final String userId;
    private final String token;

    public WhittakerResponse(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
