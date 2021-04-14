package com.xander.shutdownpc.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpProvider {
    private static OkHttpClient client;

    public static OkHttpClient getInstance() {
        if (client == null) {
            client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()).build();
        }
        return client;
    }
}
