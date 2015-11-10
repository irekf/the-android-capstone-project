package com.acpcoursera.diabetesmanagment.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

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
                                    .registerTypeAdapter(Timestamp.class, new GsonUtcDateAdapter())
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

    /* Gson uses the local timezone when serializing a Date (Timestamp in our case), we need a workaround */
    private static class GsonUtcDateAdapter  implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
        private final DateFormat dateFormat;

        private GsonUtcDateAdapter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override public synchronized JsonElement serialize(Timestamp date, Type type,
                                                            JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateFormat.format(date));
        }

        @Override public synchronized Timestamp deserialize(JsonElement jsonElement, Type type,
                                                       JsonDeserializationContext jsonDeserializationContext) {
            try {
                // TODO do I even need Timestamp in the app, Date would work as well
                return new Timestamp(dateFormat.parse(jsonElement.getAsString()).getTime());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }

}
