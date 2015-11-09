package com.acpcoursera.diabetesmanagment.service;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

import com.acpcoursera.diabetesmanagment.ui.AuthActivity;
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

        String tableToUpdate = data.getString("table");

        Log.d(TAG, "Message from: " + from);
        Log.d(TAG, "table: " + tableToUpdate);


        data.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(new Account(AuthActivity.ACCOUNT, AuthActivity.ACCOUNT_TYPE),
                AuthActivity.AUTHORITY, data);

    }

}
