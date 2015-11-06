package com.acpcoursera.diabetesmanagment.model;

import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_ADDRESS;
import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_PORT;
import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_SCHEME;

public class DmService {

    private static String TAG = DmService.class.getSimpleName();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(
                    new HttpUrl.Builder()
                            .scheme(SERVER_SCHEME)
                            .host(SERVER_ADDRESS)
                            .port(SERVER_PORT)
                            .build())
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss.S")
                            .create())
            );

    public static DmServiceProxy createService(OkHttpClient client) {
        return builder.client(client).build().create(DmServiceProxy.class);
    }

    public static DmServiceProxy createService(OkHttpClient client, final String accessToken) {
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request oldRequest = chain.request();
                Request newRequest = oldRequest.newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        return builder.client(client).build().create(DmServiceProxy.class);
    }

}
