package com.localz.pinch.models;

import org.json.JSONObject;

public class HttpRequest {
    public String endpoint;
    public String method;
    public JSONObject headers;
    public String body;
    public String[] certFilenames;
    public int timeout;

    private static final int DEFAULT_TIMEOUT = 10000;

    public HttpRequest() {
        this.timeout = DEFAULT_TIMEOUT;
    }

    public HttpRequest(String endpoint) {
        this.endpoint = endpoint;
        this.timeout = DEFAULT_TIMEOUT;
    }

    public HttpRequest(String endpoint, String method, JSONObject headers, String body, String[] certFilenames, int timeout) {
        this.endpoint = endpoint;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.certFilenames = certFilenames;
        this.timeout = timeout;
    }
}
