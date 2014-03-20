package com.pearson.lt.mobileplatform.android.core.whittaker.requests.models;

import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by chavry on 9/5/13.
 */
public class WhittakerTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private String expiresIn;
    private String windmill;
    @JsonProperty("grant_type")
    private String grantType;

    public WhittakerTokenResponse() {

    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getWindmill() {
        return windmill;
    }
}
