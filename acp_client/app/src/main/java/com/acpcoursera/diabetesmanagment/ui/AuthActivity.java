package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.GcmRegistrationIntentService;

/*
Send a test push notification:
curl -H "Content-Type:application/json" -H "Authorization:key=KEY_FROM_GOOGLE_DEV_CONSOLE" \
-d '{"to" : "REGISTRATION_TOKEN", "data" : {"message" : "Hello, Irek!"} }' \
https://gcm-http.googleapis.com/gcm/send
 */

public class AuthActivity extends Activity implements AuthFragment.Callbacks {

    private static String TAG = AuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);

        if (savedInstanceState != null) {
            return;
        }

        AuthFragment authFragment = new AuthFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.auth_activity_fragment_container, authFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onLogInButtonClicked() {
        LogInFragment logInFragment = new LogInFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_activity_fragment_container, logInFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSignUpButtonClicked() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_activity_fragment_container, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
