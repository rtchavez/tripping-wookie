package com.pearson.lt.mobileplatform.android.core.utils;

import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.HttpRequestFactory;
import com.pearson.lt.mobileplatform.android.shaded.google.api.client.http.javanet.NetHttpTransport;

public final class ServerUtilities {

    public static HttpRequestFactory createNetHttpRequestFactory() {
        NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
        NetHttpTransport transport = builder.build();
        return transport.createRequestFactory();
    }

}
