package com.xander.shutdownpc.utils;

import okhttp3.OkHttpClient;

public class OkHttpProvider {
    private static OkHttpClient client;

    public static OkHttpClient getInstance() {
        if (client == null) {
            client = new OkHttpClient.Builder().build();
        }
        return client;
    }
}
