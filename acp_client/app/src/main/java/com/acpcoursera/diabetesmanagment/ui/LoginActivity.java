package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.GcmRegistrationIntentService;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import static com.acpcoursera.diabetesmanagment.util.NetUtils.getSecureHttpClient;

/*
Send a test push notification:
curl -H "Content-Type:application/json" -H "Authorization:key=KEY_FROM_GOOGLE_DEV_CONSOLE" \
-d '{"to" : "REGISTRATION_TOKEN", "data" : {"message" : "Hello, Irek!"} }' \
https://gcm-http.googleapis.com/gcm/send
 */

public class LoginActivity extends Activity {

    private static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);

        OkHttpClient httpClient = getSecureHttpClient(getApplicationContext());
        Log.d(TAG, "HTTP client = " + httpClient);

        Request request = new Request.Builder().url("https://192.168.0.102:8443/signup/test").build();
        final Call call = httpClient.newCall(request);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Response response = null;
                try {
                    Log.d(TAG, "about to call https://192.168.0.102:8443/signup/test");
                    response = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, response.message());

                return null;
            }
        }.execute();

    }


}
