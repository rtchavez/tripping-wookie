package com.pearson.lt.mobileplatform.android.core;

import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.SpiceManager;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.request.CachedSpiceRequest;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.request.SpiceRequest;
import com.pearson.lt.mobileplatform.android.shaded.octo.android.robospice.request.listener.RequestListener;

public class AndroidSpiceManager extends SpiceManager {

	public AndroidSpiceManager() {
		super(AndroidSpiceService.class);
	}
	
	@Override
	public <T> void execute(SpiceRequest<T> request, RequestListener<T> requestListener) {
		super.execute(request, requestListener);
	}
	
	@Override
	public <T> void execute(CachedSpiceRequest<T> cachedSpiceRequest, RequestListener<T> requestListener) {
		super.execute(cachedSpiceRequest, requestListener);
	}
	
	@Override
	public <T> void execute(SpiceRequest<T> request, Object requestCacheKey, long cacheExpiryDuration, RequestListener<T> requestListener) {
		super.execute(request, requestCacheKey, cacheExpiryDuration, requestListener);
	}

}
