package com.pearson.lt.mobileplatform.android.push.gcm;

import android.text.TextUtils;

import com.pearson.lt.mobileplatform.android.core.exception.AndroidCoreException;

public final class GCMSettings {

    private String senderId;

    GCMSettings() {
    }

    void initialize(final String senderId) {
        if (TextUtils.isEmpty(senderId)) {
            throw new AndroidCoreException("Invalid settings.");
        }
        this.senderId = senderId;

    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

}
