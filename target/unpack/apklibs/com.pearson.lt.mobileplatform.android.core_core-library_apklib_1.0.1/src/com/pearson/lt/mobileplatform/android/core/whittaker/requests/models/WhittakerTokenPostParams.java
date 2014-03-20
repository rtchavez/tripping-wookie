package com.pearson.lt.mobileplatform.android.core.whittaker.requests.models;

/**
 * Created by chavry on 9/5/13.
 */
public class WhittakerTokenPostParams {

    private String email;
    private String password;

    public WhittakerTokenPostParams() {

    }

    public WhittakerTokenPostParams(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
