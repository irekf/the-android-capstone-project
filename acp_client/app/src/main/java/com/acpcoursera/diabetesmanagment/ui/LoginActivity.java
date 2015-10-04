package com.acpcoursera.diabetesmanagment.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.GcmRegistrationIntentService;

/*
Send a test push notification:
curl -H "Content-Type:application/json" -H "Authorization:key=KEY_FROM_GOOGLE_DEV_CONSOLE" \
-d '{"to" : "REGISTRATION_TOKEN", "data" : {"message" : "Hello, Irek!"} }' \
https://gcm-http.googleapis.com/gcm/send
 */

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);

    }
}
