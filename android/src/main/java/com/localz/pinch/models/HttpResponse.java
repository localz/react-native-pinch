package com.localz.pinch.models;

import com.facebook.react.bridge.WritableMap;

public class HttpResponse {
    public int statusCode;
    public WritableMap headers;
    public String bodyString;
    public String statusText;

    public HttpResponse() {}

    public HttpResponse(int statusCode, WritableMap headers, String bodyString, String statusText) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.bodyString = bodyString;
        this.statusText = statusText;
    }
}
