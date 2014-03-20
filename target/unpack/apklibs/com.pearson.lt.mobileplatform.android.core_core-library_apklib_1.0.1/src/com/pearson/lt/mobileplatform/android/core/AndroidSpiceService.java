package com.pearson.lt.mobileplatform.android.core;

import android.app.Application;

import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.GoogleHttpClientSpiceService;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.networkstate.NetworkStateChecker;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.persistence.CacheManager;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.persistence.exception.CacheCreationException;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.persistence.googlehttpclient.json.Jackson2ObjectPersisterFactory;
import com.pearson.lt.mobileplatform.android.core.networkstate.AlwaysAvailableNetworkStateChecker;

import java.io.File;

public class AndroidSpiceService extends GoogleHttpClientSpiceService {

    public static final String LOG_TAG = AndroidSpiceService.class.getName();
    private static final int THREAD_COUNT = 2;

    private final AlwaysAvailableNetworkStateChecker alwaysAvailableNetworkStateChecker = new AlwaysAvailableNetworkStateChecker();

    public AndroidSpiceService() {
    }

    @Override
    public int getThreadCount() {
        return THREAD_COUNT;
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();

        if (application.getBaseContext() != null) {
            File cacheFolder = application.getCacheDir();
            Jackson2ObjectPersisterFactory jacksonObjectPersisterFactory = new Jackson2ObjectPersisterFactory(application, cacheFolder);
            cacheManager.addPersister(jacksonObjectPersisterFactory);
        }

        return cacheManager;
    }

    @Override
    protected NetworkStateChecker getNetworkStateChecker() {
        return alwaysAvailableNetworkStateChecker;
    }

    @Override
    public boolean isFailOnCacheError() {
        return true;
    }


}
