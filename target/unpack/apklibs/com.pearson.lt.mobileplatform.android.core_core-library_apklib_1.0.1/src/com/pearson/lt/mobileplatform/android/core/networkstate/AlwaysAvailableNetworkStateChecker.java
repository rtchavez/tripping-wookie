package com.pearson.lt.mobileplatform.android.core.networkstate;

import android.content.Context;

import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.networkstate.NetworkStateChecker;

/**
 * Created by chavry on 10/10/13.
 */
public class AlwaysAvailableNetworkStateChecker implements NetworkStateChecker {

    /**
     * Determine whether network is available or not.
     *
     * @param context the context from which network state is accessed.
     * @return a boolean indicating if network is considered to be available or
     * not.
     */
    @Override
    public boolean isNetworkAvailable(Context context) {
        return true;
    }

    /**
     * Check if all permissions necessary to determine network state and use
     * network are granted to a given context.
     *
     * @param context the context that will be checked to see if it has all required
     *                permissions.
     */
    @Override
    public void checkPermissions(Context context) {

    }

}
