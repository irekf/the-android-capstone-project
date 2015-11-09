package com.acpcoursera.diabetesmanagment.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.GcmRegistrationIntentService;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

/*
Send a test push notification:
curl -H "Content-Type:application/json" -H "Authorization:key=KEY_FROM_GOOGLE_DEV_CONSOLE" \
-d '{"to" : "REGISTRATION_TOKEN", "data" : {"message" : "Hello, Irek!"} }' \
https://gcm-http.googleapis.com/gcm/send
 */

public class AuthActivity extends FragmentActivity implements LogInFragment.Callbacks {

    private static String TAG = AuthActivity.class.getSimpleName();

    public static final String AUTHORITY = "com.acpcoursera.diabetesmanagement.provider.dmprovider";
    public static final String ACCOUNT_TYPE = "com.acpcoursera";
    public static final String ACCOUNT = "dummyaccount";
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);

        if (savedInstanceState != null) {
            return;
        }

        // start the main activity if we are logged in
        if (MiscUtils.isLoggedIn(getApplicationContext())) {
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivityIntent);
        }

        LogInFragment logInFragment = new LogInFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.auth_activity_fragment_container, logInFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onSignUpButtonClicked() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_activity_fragment_container, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /* Helper method for the sync adapter */
    public static Account CreateSyncAccount(Context context) {

        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return newAccount;
    }

}
