package com.acpcoursera.diabetesmanagment.util;

import android.content.Context;

import com.acpcoursera.diabetesmanagment.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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
import javax.security.auth.login.LoginException;

import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.*;

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

    public static String getAccessToken(OkHttpClient client, String username, String password)
            throws IOException, LoginException {

        HttpUrl url = new HttpUrl.Builder()
                .scheme(SERVER_SCHEME)
                .host(SERVER_ADDRESS)
                .port(SERVER_PORT)
                .addPathSegment("oauth")
                .addEncodedPathSegment("token")
                .addQueryParameter("username", username)
                .addQueryParameter("password", password)
                .addQueryParameter("scope", "read write")
                .addQueryParameter("grant_type", "password")
                .addQueryParameter("client_id", SERVER_CLIENT_ID)
                .addQueryParameter("client_secret", SERVER_CLIENT_SECRET)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", Credentials.basic(SERVER_CLIENT_ID, SERVER_CLIENT_SECRET))
                .post(RequestBody.create(MediaType.parse(""), ""))
                .build();

        Response response = client.newCall(request).execute();

        int httpStatus = response.code();
        if (httpStatus < 200 || httpStatus > 299) {
            throw new LoginException("Error: cannot get access token, HTTP status = " + httpStatus);
        }

        String body = response.body().string();
        JsonElement accessToken = new JsonParser().parse(body).getAsJsonObject().get("access_token");
        if (accessToken == null) {
            throw new LoginException("Error: no access token found in response body");
        }

        return accessToken.getAsString();
    }

    private static KeyStore readKeyStore(Context context) throws KeyStoreException {
        KeyStore trusted = KeyStore.getInstance("BKS");
        InputStream in = context.getResources().openRawResource(R.raw.acp);
        try {
            // Initialize the keystore with the provided trusted certificates
            // Also provide the password of the keystore
            trusted.load(in, CLIENT_KEYSTORE_PASS.toCharArray());
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
