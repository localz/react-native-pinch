package com.localz;

import android.os.AsyncTask;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.UnexpectedNativeTypeException;
import com.facebook.react.bridge.WritableMap;

import com.localz.pinch.models.HttpRequest;
import com.localz.pinch.models.HttpResponse;
import com.localz.pinch.utils.HttpUtil;
import com.localz.pinch.utils.JsonUtil;

import org.json.JSONException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class RNPinch extends ReactContextBaseJavaModule {

    private static final String OPT_METHOD_KEY = "method";
    private static final String OPT_HEADER_KEY = "headers";
    private static final String OPT_BODY_KEY = "body";
    private static final String OPT_SSL_PINNING_KEY = "sslPinning";
    private static final String OPT_SSL_PLAIN_TEXT = "plainText";
    private static final String OPT_TIMEOUT_KEY = "timeoutInterval";
    private static final String OPT_REQUEST_CERT_KEY = "requestCert";
    private static final String OPT_P12_PACK_KEY = "p12pack";
    private static final String OPT_P12_PASS_KEY = "p12pass";

    private HttpUtil httpUtil;
    private String packageName = null;
    private String displayName = null;
    private String version = null;
    private String versionCode = null;

    public RNPinch(ReactApplicationContext reactContext) {
        super(reactContext);
        httpUtil = new HttpUtil();
        try {
            PackageManager pManager = reactContext.getPackageManager();
            packageName = reactContext.getPackageName();
            PackageInfo pInfo = pManager.getPackageInfo(packageName, 0);
            ApplicationInfo aInfo = pManager.getApplicationInfo(packageName, 0);
            displayName = pManager.getApplicationLabel(aInfo).toString();
            version = pInfo.versionName;
            versionCode = String.valueOf(pInfo.versionCode);
        } catch (NameNotFoundException nnfe) {
            System.out.println("RNAppInfo: package name not found");
        }
    }

    @Override
    public String getName() {
        return "RNPinch";
    }

    @ReactMethod
    public void fetch(String endpoint, ReadableMap opts, Callback callback) {
        new FetchTask(opts, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, endpoint);
    }

    private class FetchTask extends AsyncTask<String, Void, WritableMap> {
        private ReadableMap opts;
        private Callback callback;

        public FetchTask(ReadableMap opts, Callback callback) {
            this.opts = opts;
            this.callback = callback;
        }

        @Override
        protected WritableMap doInBackground(String... endpoint) {

            try {
                WritableMap response = Arguments.createMap();
                HttpRequest request = new HttpRequest(endpoint[0]);

                if (opts.hasKey(OPT_BODY_KEY)) {
                    request.body = opts.getString(OPT_BODY_KEY);
                }
                if (opts.hasKey(OPT_METHOD_KEY)) {
                    request.method = opts.getString(OPT_METHOD_KEY);
                }
                if (opts.hasKey(OPT_HEADER_KEY)) {
                    request.headers = JsonUtil.convertReadableMapToJson(opts.getMap(OPT_HEADER_KEY));
                }
                if (opts.hasKey(OPT_SSL_PINNING_KEY)) {
                    if (opts.getMap(OPT_SSL_PINNING_KEY).hasKey(OPT_SSL_PLAIN_TEXT)
                            && opts.getMap(OPT_SSL_PINNING_KEY).getBoolean(OPT_SSL_PLAIN_TEXT)
                    ) {
                        if (opts.getMap(OPT_SSL_PINNING_KEY).hasKey("cert")) {
                            ReadableMap cert = opts.getMap(OPT_SSL_PINNING_KEY).getMap("cert");
                            request.certMaps = new ReadableMap[]{cert};
                        } else if (opts.getMap(OPT_SSL_PINNING_KEY).hasKey("certs")) {
                            ReadableArray certMaps = opts.getMap(OPT_SSL_PINNING_KEY).getArray("certs");
                            ReadableMap[] certs = new ReadableMap[certMaps.size()];
                            for (int i = 0; i < certMaps.size(); i++) {
                                certs[i] = certMaps.getMap(i);
                            }
                            request.certMaps = certs;
                        }
                    } else {
                        if (opts.getMap(OPT_SSL_PINNING_KEY).hasKey("cert")) {
                            String fileName = opts.getMap(OPT_SSL_PINNING_KEY).getString("cert");
                            request.certFilenames = new String[]{fileName};
                        } else if (opts.getMap(OPT_SSL_PINNING_KEY).hasKey("certs")) {
                            ReadableArray certsStrings = opts.getMap(OPT_SSL_PINNING_KEY).getArray("certs");
                            String[] certs = new String[certsStrings.size()];
                            for (int i = 0; i < certsStrings.size(); i++) {
                                certs[i] = certsStrings.getString(i);
                            }
                            request.certFilenames = certs;
                        }
                    }
                }
                if (opts.hasKey(OPT_TIMEOUT_KEY)) {
                    request.timeout = opts.getInt(OPT_TIMEOUT_KEY);
                }
                if (opts.getMap(OPT_SSL_PINNING_KEY).hasKey(OPT_REQUEST_CERT_KEY)
                        && opts.getMap(OPT_SSL_PINNING_KEY).getBoolean(OPT_REQUEST_CERT_KEY)) {
                    request.requestCert = true;
                    request.p12pack = opts.getMap(OPT_SSL_PINNING_KEY).getString(OPT_P12_PACK_KEY);
                    request.p12pass = opts.getMap(OPT_SSL_PINNING_KEY).getString(OPT_P12_PASS_KEY);
                }
                else {
                    request.requestCert = false;
                    request.p12pack = "";
                    request.p12pass = "";
                }

                HttpResponse httpResponse;
                if (request.endpoint.toLowerCase().startsWith("https:")) {
                    httpResponse = httpUtil.sendHttpsRequest(request);
                } else {
                    httpResponse = httpUtil.sendHttpRequest(request);
                }

                response.putInt("status", httpResponse.statusCode);
                response.putString("statusText", httpResponse.statusText);
                response.putString("bodyString", httpResponse.bodyString);
                response.putMap("headers", httpResponse.headers);

                return response;
            } catch(JSONException | IOException | UnexpectedNativeTypeException | KeyStoreException | CertificateException | KeyManagementException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                WritableMap error = Arguments.createMap();
                error.putString("errorMessage", e.toString());
                return error;
            }
        }

        @Override
        protected void onPostExecute(WritableMap response) {

            if (response.hasKey("errorMessage")) {
                callback.invoke(response.getString("errorMessage"), null);
            } else {
                callback.invoke(null, response);
            }
        }
    }
}
