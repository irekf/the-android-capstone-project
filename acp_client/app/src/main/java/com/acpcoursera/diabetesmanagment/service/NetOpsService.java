package com.acpcoursera.diabetesmanagment.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.acpcoursera.diabetesmanagment.util.NetUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

import javax.security.auth.login.LoginException;

public class NetOpsService extends IntentService {

    private static String TAG = NetOpsService.class.getSimpleName();

    public static String ACTION_SIGN_UP = "action_sign_up";
    public static String ACTION_LOG_IN = "action_log_in";

    public static String EXTRA_USER_NAME = "user_name";
    public static String EXTRA_PASSWORD = "password";
    public static String EXTRA_ACCESS_TOKEN = "access_token";
    public static String EXTRA_ERROR_MESSAGE = "error_message";

    public static String RESULT_CODE = "result_code";
    public static int RC_OK = 0;
    public static int RC_ERROR = 8;
    public static int RC_MISSING = 16;

    public static OkHttpClient httpClient;

    public NetOpsService() {
        super("NetOpsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (httpClient == null) {
            try {
                httpClient = NetUtils.getSecureHttpClient(getApplicationContext());
            } catch (NetUtils.SecureHttpClientException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getAction();
        Intent broadcastIntent = new Intent();
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.setAction(action);

        if (httpClient != null) {
            if (action.equals(ACTION_LOG_IN)) {
                String userName = intent.getStringExtra(EXTRA_USER_NAME);
                String password = intent.getStringExtra(EXTRA_PASSWORD);
                try {
                    String accessToken = NetUtils.getAccessToken(httpClient, userName, password);
                    broadcastIntent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
                    broadcastIntent.putExtra(RESULT_CODE, RC_OK);
                } catch (IOException | LoginException e) {
                    broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
                    broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
                    e.printStackTrace();
                }
            }
            else if (action.equals(ACTION_SIGN_UP)) {

            }
            else {

            }
        }
        else {
            broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
            broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, "HTTP client null");
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
    }
}
