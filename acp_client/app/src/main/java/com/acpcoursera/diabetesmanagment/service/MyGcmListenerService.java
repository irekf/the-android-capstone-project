package com.acpcoursera.diabetesmanagment.service;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import com.acpcoursera.diabetesmanagment.provider.DmContract;
import com.google.android.gms.gcm.GcmListenerService;

/*

curl -H "Content-Type:application/json" -H "Authorization:key=KEY_FROM_GOOGLE_DEV_CONSOLE" \
-d '{"to" : "REGISTRATION_TOKEN", "data" : {"follower_name" : "Helen"} }' https://gcm-http.googleapis.com/gcm/send

Dev Console: API manager -> Credentials..

 */

public class MyGcmListenerService extends GcmListenerService {

    private static String TAG = MyGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String follower = data.getString("follower_name");

        Log.d(TAG, "Message from: " + from);
        Log.d(TAG, "Follower's name: " + follower);

        ContentValues values = new ContentValues();
        values.put(DmContract.Followers.USERNAME, "me");
        values.put(DmContract.Followers.FOLLOWER_NAME, follower);
        values.put(DmContract.Followers.ACCEPTED, 1);
        values.put(DmContract.Followers.MAJOR_DATA, 1);
        values.put(DmContract.Followers.MINOR_DATE, 0);

        getContentResolver().insert(DmContract.Followers.buildFollowersUri(), values);

    }

}
