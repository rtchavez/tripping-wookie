package com.pearson.lt.mobileplatform.android.core.whittaker;

import android.text.TextUtils;

import com.pearson.lt.mobileplatform.android.core.exception.AndroidCoreException;
import com.pearson.lt.mobileplatform.android.core.tasks.TaskQueue;
import com.pearson.lt.mobileplatform.android.core.whittaker.tasks.WhittakerResponseTask;

/**
 * Created by chavry on 10/24/13.
 */
public class WhittakerUtility {

    private static String email;
    private static String password;
    private static String clientId;
    private static String shared;

    private WhittakerUtility() {

    }

    public static void init(final String email, final String password, final String clientId, final String shared) {
        WhittakerUtility.email = email;
        WhittakerUtility.password = password;
        WhittakerUtility.clientId = clientId;
        WhittakerUtility.shared = shared;
    }

    public static void requestWhittakerAuthentication(final WhittakerResponseListener listener) {
        if (TextUtils.isEmpty(clientId) || TextUtils.isEmpty(shared)) {
            throw new AndroidCoreException("Illegal authorization parameters.");
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            throw new AndroidCoreException("Illegal credentials.");
        }

        final TaskQueue taskQueue = new TaskQueue(true);
        final WhittakerResponseTask task = new WhittakerResponseTask(email, password, clientId, shared, listener);
        taskQueue.addTask(task).start();
    }

}
