package com.localz.pinch.models;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONObject;

public class HttpRequest {
    public String endpoint;
    public String method;
    public JSONObject headers;
    public String body;
    public String[] certFilenames;
    public ReadableMap[] certMaps;
    public boolean requestCert;
    public String p12pack;
    public String p12pass;
    public int timeout;

    private static final int DEFAULT_TIMEOUT = 10000;

    public HttpRequest() {
        this.timeout = DEFAULT_TIMEOUT;
    }

    public HttpRequest(String endpoint) {
        this.endpoint = endpoint;
        this.timeout = DEFAULT_TIMEOUT;
    }

    public HttpRequest(String endpoint, String method, JSONObject headers, String body, String[] certFilenames, Boolean requestCert, String p12pack, String p12pass, int timeout) {
        this.endpoint = endpoint;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.certFilenames = certFilenames;
        this.requestCert = requestCert;
        this.p12pack = p12pack;
        this.p12pass = p12pass;
        this.timeout = timeout;
    }

    public HttpRequest(String endpoint, String method, JSONObject headers, String body, ReadableMap[] certMaps, Boolean requestCert, String p12pack, String p12pass, int timeout) {
        this.endpoint = endpoint;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.certMaps = certMaps;
        this.requestCert = requestCert;
        this.p12pack = p12pack;
        this.p12pass = p12pass;
        this.timeout = timeout;
    }
}
