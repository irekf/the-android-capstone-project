package com.acpcoursera.diabetesmanagment.util;

import android.content.Context;

import com.acpcoursera.diabetesmanagment.R;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class NetUtils {

    private static String TAG = NetUtils.class.getSimpleName();

    public static class SecureHttpClientException extends Exception {

        private Throwable rootCause;

        public SecureHttpClientException() {
            super();
        }

        public SecureHttpClientException(String message) {
            super(message);
        }

        public SecureHttpClientException(Throwable rootCause) {
            super(rootCause);
            this.rootCause = rootCause;
        }

        public SecureHttpClientException(String message, Throwable rootCause) {
            super(message, rootCause);
            this.rootCause = rootCause;
        }

        public Throwable getRootCause() {
            return rootCause;
        }
    }

    public static OkHttpClient getSecureHttpClient(Context context) throws SecureHttpClientException {
        OkHttpClient client = new OkHttpClient();

        KeyStore keyStore = null;
        try {
            keyStore = readKeyStore(context);
        } catch (Throwable t) {
            throw new SecureHttpClientException("Error: " + t.getMessage(), t);
        }

        SSLContext sslContext = null;
        TrustManagerFactory trustManagerFactory = null;
        KeyManagerFactory keyManagerFactory = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } catch (Throwable t) {
            throw new SecureHttpClientException("Error: " + t.getMessage(), t);
        }

        try {
            trustManagerFactory.init(keyStore);
            keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
        } catch (Throwable t) {
            throw new SecureHttpClientException("Error: " + t.getMessage(), t);
        }

        try {
            sslContext.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom());
        } catch (Throwable t) {
            throw new SecureHttpClientException("Error: " + t.getMessage(), t);
        }
        client.setSslSocketFactory(sslContext.getSocketFactory());

        /* The certificate host name is localhost, I have to use the actual IP 192.168.0.102,
         * that's why the host name should be ignored. Otherwise the following exception will
         * be received: javax.net.ssl.SSLPeerUnverifiedException: Hostname not verified */
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        return client;
    }

    private static KeyStore readKeyStore(Context context) throws KeyStoreException {
        KeyStore trusted = KeyStore.getInstance("BKS");
        InputStream in = context.getResources().openRawResource(R.raw.acp);
        try {
            // Initialize the keystore with the provided trusted certificates
            // Also provide the password of the keystore
            trusted.load(in, "acppass".toCharArray());
        } catch (Throwable t) {
            throw new KeyStoreException("Error: " + t.getMessage(), t);
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return trusted;
    }

}
