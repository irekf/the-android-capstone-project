package com.acpcoursera.diabetesmanagment.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.config.AcpPreferences;
import com.acpcoursera.diabetesmanagment.model.DmService;
import com.acpcoursera.diabetesmanagment.model.DmServiceProxy;
import com.acpcoursera.diabetesmanagment.model.UserInfo;
import com.acpcoursera.diabetesmanagment.util.NetUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import retrofit.Call;
import retrofit.Response;

public class NetOpsService extends IntentService {

    private static String TAG = NetOpsService.class.getSimpleName();

    public static String ACTION_SIGN_UP = "action_sign_up";
    public static String ACTION_LOG_IN = "action_log_in";

    public static String EXTRA_USER_NAME = "user_name";
    public static String EXTRA_PASSWORD = "password";
    public static String EXTRA_ACCESS_TOKEN = "access_token";

    public static String EXTRA_USER_INFO = "user_info";

    public static String EXTRA_ERROR_MESSAGE = "error_message";

    public static String RESULT_CODE = "result_code";
    public static int RC_OK = 0;
    public static int RC_ERROR = 8;
    public static int RC_MISSING = 16;

    public static OkHttpClient httpClient;
    private static DmServiceProxy dmService;

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

        if (dmService == null) {

        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (AcpPreferences.NET_DELAY_SEC != 0) {
            try {
                Thread.sleep(AcpPreferences.NET_DELAY_SEC * 1000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
                DmServiceProxy svc = DmService.createService(httpClient);
                UserInfo signUpInfo = intent.getParcelableExtra(EXTRA_USER_INFO);
                Call<Void> call = svc.signUp(signUpInfo);
                try {
                    Response<Void> response = call.execute();
                    if (response.code() < 200 || response.code() > 299) {
                        broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
                        broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, response.message());
                    }
                    else {
                        broadcastIntent.putExtra(RESULT_CODE, RC_OK);
                    }
                } catch (IOException e) {
                    broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
                    broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
                    e.printStackTrace();
                }
            }
            else {

            }
        } else {
            broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
            broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, getString(R.string.error_null_http_client));
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
    }
}
