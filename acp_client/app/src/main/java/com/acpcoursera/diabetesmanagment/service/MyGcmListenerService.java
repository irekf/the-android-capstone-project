package com.acpcoursera.diabetesmanagment.service;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static String TAG = MyGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");

        Log.d(TAG, "Message from: " + from);
        Log.d(TAG, "Message received: " + message);

    }

}
