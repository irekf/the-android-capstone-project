package com.acpcoursera.diabetesmanagment.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.config.AcpPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GcmRegistrationIntentService extends IntentService {

    private static String TAG = GcmRegistrationIntentService.class.getSimpleName();

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

        }
        catch (Exception e) {
            Log.d(TAG, "Failed to refresh GCM Token", e);
            sharedPreferences.edit().putBoolean(AcpPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

}
