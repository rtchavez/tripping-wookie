package com.pearson.lt.mobileplatform.android.push.gcm;

import android.content.Context;

import com.pearson.lt.mobileplatform.appservices.android.sdk.ApigeeClient;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.entities.Device;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.entities.User;

/**
 * Created by Ryan Chavez on 9/11/13.
 */
public class PushClient extends ApigeeClient {

    private PushDataClient pushDataClient;
    private Device device;
    private User user;

    public PushClient(String organizationId, String applicationId, String baseURL, Context context) {
        super(organizationId, applicationId, baseURL, context);
        pushDataClient = new PushDataClient(organizationId, applicationId, baseURL);
    }

    @Override
    public PushDataClient getDataClient() {
        return pushDataClient;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
