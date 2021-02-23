package com.localz.pinch.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.facebook.react.bridge.ReadableMap;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class KeyPinStoreUtil {

    private static HashMap<String[], KeyPinStoreUtil> instanceStrings = new HashMap<>();
    private static HashMap<ReadableMap[], KeyPinStoreUtil> instanceMaps = new HashMap<>();
    private SSLContext sslContext = SSLContext.getInstance("TLS");

    public static synchronized KeyPinStoreUtil getInstance(String[] filenames) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (filenames != null && instanceStrings.get(filenames) == null) {
            instanceStrings.put(filenames, new KeyPinStoreUtil(filenames));
        }
        return instanceStrings.get(filenames);
    }

    private KeyPinStoreUtil(String[] filenames) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Create a KeyStore for our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        for (String filename : filenames) {
            InputStream caInput = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("assets/" + filename + ".cer"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            keyStore.setCertificateEntry(filename, ca);
        }

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        sslContext.init(null, tmf.getTrustManagers(), null);
    }

    public static synchronized KeyPinStoreUtil getInstance(ReadableMap[] maps) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (maps != null && instanceMaps.get(maps) == null) {
            instanceMaps.put(maps, new KeyPinStoreUtil(maps));
        }
        return instanceMaps.get(maps);
    }

    private KeyPinStoreUtil(ReadableMap[] maps) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Create a KeyStore for our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        for (ReadableMap map : maps) {
            String cert = map.getString("cert");
            String name = map.getString("name");
            InputStream caInput = new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            keyStore.setCertificateEntry(name, ca);
        }

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        sslContext.init(null, tmf.getTrustManagers(), null);
    }

    public SSLContext getContext() {
        return sslContext;
    }
}
