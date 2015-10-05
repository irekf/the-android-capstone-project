package com.acpcoursera.diabetesmanagment.util;

import android.content.Context;

import com.acpcoursera.diabetesmanagment.R;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class NetUtils {

    public static OkHttpClient getSecureHttpClient(Context context) {
        OkHttpClient client = new OkHttpClient();

        KeyStore keyStore = null;
        try {
            keyStore = readKeyStore(context);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        SSLContext sslContext = null;
        TrustManagerFactory trustManagerFactory = null;
        KeyManagerFactory keyManagerFactory = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            trustManagerFactory.init(keyStore);
            keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        try {
            sslContext.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
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
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return trusted;
    }

}
