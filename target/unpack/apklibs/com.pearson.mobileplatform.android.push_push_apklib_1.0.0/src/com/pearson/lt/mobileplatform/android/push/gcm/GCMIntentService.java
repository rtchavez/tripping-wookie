package com.pearson.lt.mobileplatform.android.push.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gcm.GCMBaseIntentService;
import com.pearson.lt.mobileplatform.android.core.json.JsonUtils;
import com.pearson.lt.mobileplatform.android.core.logging.Logger;
import com.pearson.lt.mobileplatform.android.core.logging.Logger.LogLevel;
import com.pearson.lt.mobileplatform.android.core.utils.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String LOG_TAG = "GCMIntentService";
    private static final String ACTION_RECEIVE = "com.google.android.c2dm.intent.RECEIVE";

    public GCMIntentService() {
        super(PushNotifications.getSettings().getSenderId());
    }

    @Override
    protected String[] getSenderIds(Context context) {
        return super.getSenderIds(context);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        PushNotifications.dispatchDeviceRegistered(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        PushNotifications.dispatchDeviceUnregistered(context);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        if (PushNotifications.userIsRegisteredForNotifications(context)) {
            final String action = intent.getAction();
            final Bundle bundle = intent.getExtras();
            if (ACTION_RECEIVE.equals(action)) {
                final String json = getJsonFromPayload(bundle);
                PushNotifications.dispatchNotification(context, json);
            }
        }
    }

    private String getJsonFromPayload(Bundle bundle) {
        Set<String> keys = bundle.keySet();
        Iterator<String> iterator = keys.iterator();
        Map<String, Object> values = Maps.newHashMap();

        while (iterator.hasNext()) {
            String key = iterator.next();
            if ("collapse_key".equals(key) || "from".equals(key)) {
                continue;
            }
            Object value = bundle.get(key);
            values.put(key, value);

            if (Logger.isLoggable(LogLevel.DEBUG)) {
                Logger.d(LOG_TAG, String.format("%s %s", key, String.valueOf(value)));
            }
        }

        iterator = values.keySet().iterator();
        StringBuilder json = new StringBuilder("{");
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = values.get(key);
            json.append("\"" + key + "\":");
            if (value == null) {
                json.append("null");
            } else {
                String data = String.valueOf(value);
                if (JsonUtils.isJsonObject(data)) {
                    json.append(data);
                } else if (JsonUtils.isJsonArray(data)) {
                    json.append(data);
                } else {
                    if (value instanceof String) {
                        json.append("\"" + data + "\"");
                    } else {
                        json.append(data);
                    }
                }
            }

            if (iterator.hasNext()) {
                json.append(",");
            }
        }
        json.append("}");
        return json.toString();
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }

    @Override
    protected void onError(Context context, String errorId) {
        PushNotifications.dispatchNotificationError(context, errorId);
    }

}
