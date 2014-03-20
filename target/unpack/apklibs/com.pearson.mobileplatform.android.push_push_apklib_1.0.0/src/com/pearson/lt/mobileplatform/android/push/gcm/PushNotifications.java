package com.pearson.lt.mobileplatform.android.push.gcm;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMRegistrar;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.entities.Device;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.entities.User;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.response.ApiResponse;
import com.pearson.lt.mobileplatform.appservices.android.sdk.data.client.utils.DeviceUuidFactory;
import com.pearson.lt.mobileplatform.android.core.exception.AndroidCoreException;
import com.pearson.lt.mobileplatform.android.core.tasks.Task;
import com.pearson.lt.mobileplatform.android.core.tasks.TaskCallback;
import com.pearson.lt.mobileplatform.android.core.tasks.TaskQueue;

import java.util.UUID;

public final class PushNotifications {

    public static final String NOTIFICATION_RECEIVED = "com.pearson.lt.mobileplatform.android.push.intent.action.NOTIFICATION_RECEIVED";
    public static final String DEVICE_REGISTERED = "com.pearson.lt.mobileplatform.android.push.intent.action.DEVICE_REGISTERED";
    public static final String DEVICE_UNREGISTERED = "com.pearson.lt.mobileplatform.android.push.intent.action.DEVICE_UNREGISTERED";
    public static final String USER_REGISTERED = "com.pearson.lt.mobileplatform.android.push.intent.action.USER_REGISTERED";
    public static final String USER_UNREGISTERED = "com.pearson.lt.mobileplatform.android.push.intent.action.USER_UNREGISTERED";
    public static final String NOTIFICATION_ERROR = "com.pearson.lt.mobileplatform.android.push.intent.action.NOTIFICATION_ERROR";
    public static final String NOTIFICATION_RECEIVED_EXTRA = "com.pearson.lt.mobileplatform.android.push.intent.extra.NOTIFICATION_RECEIVED_EXTRA";
    public static final String NOTIFICATION_ERROR_ID_EXTRA = "com.pearson.lt.mobileplatform.android.push.intent.extra.NOTIFICATION_ERROR_ID_EXTRA";
    public static final String DEVICE_REGISTERED_EXTRA = "com.pearson.lt.mobileplatform.android.push.intent.extra.DEVICE_REGISTERED_EXTRA";
    private static final String LOG_TAG = PushNotifications.class.getSimpleName();
    private static final GCMSettings settings = new GCMSettings();
    private static PushClient pushClient;

    private PushNotifications() {
    }

    public static void initialize(Context context, String senderId, String organizationId, String applicationId, String baseUrl) {
        settings.initialize(senderId);
        pushClient = new PushClient(organizationId, applicationId, baseUrl, context);
    }

    /**
     * Checks whether the application was successfully registered for notifications.
     */
    public static boolean applicationIsRegisteredForNotifications(final Context context) {
        return GCMRegistrar.isRegistered(context);
    }

    /**
     * Checks whether the current user was successfully registered for notifications.
     */
    public static boolean userIsRegisteredForNotifications(final Context context) {
        return GCMRegistrar.isRegisteredOnServer(context);
    }

    /**
     * Gets the current registration id for application on GCM service.
     * <p/>
     * If result is empty, the registration has failed.
     *
     * @return registration id, or empty string if the registration is not
     * complete.
     */
    public static String getRegistrationId(final Context context) {
        return GCMRegistrar.getRegistrationId(context);
    }

    /**
     * Initiate messaging registration for the current application.
     */
    public static void registerApplicationForRemoteNotifications(final Context context) {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(context);
        // Make sure the manifest was properly set
        GCMRegistrar.checkManifest(context);

        final String registrationId = GCMRegistrar.getRegistrationId(context);
        if (registrationId.equals("")) {
            GCMRegistrar.register(context, settings.getSenderId());
        }
    }

    /**
     * Initiate messaging un-registration for the current application.
     */
    public static void unregisterApplicationForRemoteNotifications(final Context context) {
        GCMRegistrar.unregister(context);
    }

    public static void registerUserForRemoteNotifications(final Context context, final String notifier, final String registrationId, final String username) {
        final PushDataClient pushDataClient = pushClient.getDataClient();
        final TaskQueue taskQueue = new TaskQueue();

        Task<Boolean> register = new Task<Boolean>(new TaskCallback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                GCMRegistrar.setRegisteredOnServer(context, response);
                update();
            }

            @Override
            public void onException(Exception e) {
                GCMRegistrar.setRegisteredOnServer(context, false);
                update();
            }

            private void update() {
                taskQueue.finish();
                dispatchUserRegistered(context);
            }
        }) {

            @Override
            public Boolean performTask() {
                UUID deviceId = new DeviceUuidFactory(context).getDeviceUuid();
                Device device = pushDataClient.registerDeviceForPush(deviceId, notifier, registrationId, null);
                pushClient.setDevice(device);

                ApiResponse response = pushDataClient.createUser(username);
                User user = response.getFirstEntity(User.class);
                pushClient.setUser(user);

                if (username != null && device != null) {
                    final String deviceUuid = device.getUuid().toString();
                    pushDataClient.connectUserToDevice(username, deviceUuid);

                    return true;
                }
                return false;
            }
        };
        taskQueue.addTask(register);
        taskQueue.start();
    }

    public static void unregisterUserForRemoteNotifications(final Context context, final String notifier, final String username) {
        final PushDataClient pushDataClient = pushClient.getDataClient();
        final TaskQueue taskQueue = new TaskQueue();

        Task<Boolean> unregister = new Task<Boolean>(new TaskCallback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response) {
                    GCMRegistrar.setRegisteredOnServer(context, false);
                }
                update();
            }

            @Override
            public void onException(Exception e) {
                update();
            }

            private void update() {
                taskQueue.finish();
                dispatchUserUnregistered(context);
            }
        }) {

            @Override
            public Boolean performTask() {
                Device device = pushClient.getDevice();
                UUID deviceUuid = null;
                if (device == null) {
                    deviceUuid = new DeviceUuidFactory(context).getDeviceUuid();
                } else {
                    deviceUuid = device.getUuid();
                }

                if (username != null && deviceUuid != null) {
                    pushDataClient.registerDeviceForPush(deviceUuid, notifier, "", null);
                    pushDataClient.disconnectUserFromDevice(username, deviceUuid.toString());

                    pushClient.setUser(null);

                    return true;
                }
                return false;
            }
        };

        taskQueue.addTask(unregister);
        taskQueue.start();
    }

    static void dispatchNotification(final Context context, final String payload) {
        Intent intent = new Intent(PushNotifications.NOTIFICATION_RECEIVED);
        intent.putExtra(PushNotifications.NOTIFICATION_RECEIVED_EXTRA, payload);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    static void dispatchDeviceRegistered(final Context context, final String registrationId) {
        Intent intent = new Intent(PushNotifications.DEVICE_REGISTERED);
        intent.putExtra(PushNotifications.DEVICE_REGISTERED_EXTRA, registrationId);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    static void dispatchDeviceUnregistered(final Context context) {
        Intent intent = new Intent(PushNotifications.DEVICE_UNREGISTERED);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    static void dispatchUserRegistered(final Context context) {
        Intent intent = new Intent(PushNotifications.USER_REGISTERED);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    static void dispatchUserUnregistered(final Context context) {
        Intent intent = new Intent(PushNotifications.USER_UNREGISTERED);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    static void dispatchNotificationError(final Context context, final String errorId) {
        Intent intent = new Intent(PushNotifications.NOTIFICATION_ERROR);
        intent.putExtra(PushNotifications.NOTIFICATION_ERROR_ID_EXTRA, errorId);
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    /**
     * @return the settings
     */
    public static GCMSettings getSettings() {
        return settings;
    }

    public static PushClient getPushClient() {
        if (pushClient == null) {
            throw new AndroidCoreException("Library has not been initialized.");
        }
        return pushClient;
    }
}
