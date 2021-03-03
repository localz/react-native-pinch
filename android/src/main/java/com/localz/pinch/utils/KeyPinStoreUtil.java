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
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;

public class KeyPinStoreUtil {

    private static HashMap<String[], KeyPinStoreUtil> instanceStrings = new HashMap<>();
    private static HashMap<ReadableMap[], KeyPinStoreUtil> instanceMaps = new HashMap<>();
    private SSLContext sslContext = SSLContext.getInstance("TLS");

    public static synchronized KeyPinStoreUtil getInstance(String[] filenames, Boolean requestCert, String p12pack, String p12pass) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (filenames != null && instanceStrings.get(filenames) == null) {
            instanceStrings.put(filenames, new KeyPinStoreUtil(filenames, requestCert, p12pack, p12pass));
        }
        return instanceStrings.get(filenames);
    }

    private KeyPinStoreUtil(String[] filenames, Boolean requestCert, String p12pack, String p12pass) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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

    public static synchronized KeyPinStoreUtil getInstance(ReadableMap[] maps, Boolean requestCert, String p12pack, String p12pass) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
        if (maps != null && instanceMaps.get(maps) == null) {
            instanceMaps.put(maps, new KeyPinStoreUtil(maps, requestCert, p12pack, p12pass));
        }
        return instanceMaps.get(maps);
    }

    private KeyPinStoreUtil(ReadableMap[] maps, Boolean requestCert, String p12pack, String p12pass) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Create a KeyStore for our trusted CAs
        KeyStore keyStore;
        if (requestCert) {
            keyStore  = KeyStore.getInstance("PKCS12");
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(p12pack);
                InputStream p12stream = new ByteArrayInputStream(decodedBytes);
                try {
                    keyStore.load(p12stream, p12pass.toCharArray());
                } finally {
                    p12stream.close();
                }
            } catch (Exception e) {
                throw new UnrecoverableKeyException(e.getMessage());
            }
        } else {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
        }

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
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        if (requestCert) {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, p12pass.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } else {
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        }
    }

    public SSLContext getContext() {
        return sslContext;
    }
}
