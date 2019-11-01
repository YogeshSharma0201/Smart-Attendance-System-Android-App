package com.example.yogeshsharma.smartattendancesystem;

import android.util.Log;

import com.loopj.android.http.*;

public class Api {
    private static final String BASE_URL = "http://bf4d76b0.ngrok.io";

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        asyncHttpClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        asyncHttpClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void syncGet(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void syncPost(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
