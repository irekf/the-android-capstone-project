package com.acpcoursera.diabetesmanagment.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.config.AcpPreferences;
import com.acpcoursera.diabetesmanagment.model.AccessToken;
import com.acpcoursera.diabetesmanagment.model.DmService;
import com.acpcoursera.diabetesmanagment.model.DmServiceProxy;
import com.acpcoursera.diabetesmanagment.model.UserInfo;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;
import com.acpcoursera.diabetesmanagment.util.NetUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

import retrofit.Call;
import retrofit.Response;

import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_CLIENT_ID;
import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_CLIENT_SECRET;

public class NetOpsService extends IntentService {

    private static String TAG = NetOpsService.class.getSimpleName();

    public static String ACTION_SIGN_UP = "action_sign_up";
    public static String ACTION_LOG_IN = "action_log_in";
    public static String ACTION_LOG_OUT = "action_log_out";

    public static String EXTRA_USER_NAME = "user_name";
    public static String EXTRA_PASSWORD = "password";
    public static String EXTRA_ACCESS_TOKEN = "access_token";

    public static String EXTRA_USER_INFO = "user_info";

    public static String EXTRA_ERROR_MESSAGE = "error_message";

    public static String RESULT_CODE = "result_code";
    public static int RC_OK = 0;
    public static int RC_ERROR = 8;
    public static int RC_MISSING = 16;

    private LocalBroadcastManager mBroadcastManager;

    public static OkHttpClient httpClient;
    private static DmServiceProxy dmServiceUnsecured;
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

        if (dmServiceUnsecured == null) {
            dmServiceUnsecured = DmService.createService(httpClient.clone());
        }

        if (dmService == null) {
            String accessToken = MiscUtils.readAccessToken(getApplicationContext());
            if (accessToken != null) {
                dmService = DmService.createService(httpClient.clone(), accessToken);
            }
        }

        mBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

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
        if (httpClient != null) {
            if (action.equals(ACTION_LOG_IN)) {
                handleLogIn(intent);
            }
            else if (action.equals(ACTION_LOG_OUT)) {
                handleLogOut(intent);
            }
            else if (action.equals(ACTION_SIGN_UP)) {
                handleSignUp(intent);
            }
            else {

            }
        } else {
            Intent reply = new Intent(ACTION_LOG_IN);
            reply.addCategory(Intent.CATEGORY_DEFAULT);
            reply.putExtra(RESULT_CODE, RC_ERROR);
            reply.putExtra(EXTRA_ERROR_MESSAGE, getString(R.string.error_null_http_client));
            mBroadcastManager.sendBroadcast(reply);
        }

    }

    private void handleLogIn(Intent callerIntent) {

        Intent reply = new Intent(ACTION_LOG_IN);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        String userName = callerIntent.getStringExtra(EXTRA_USER_NAME);
        String password = callerIntent.getStringExtra(EXTRA_PASSWORD);

        Call<AccessToken> loginCall =
                dmServiceUnsecured.login(
                        Credentials.basic(SERVER_CLIENT_ID, SERVER_CLIENT_SECRET),
                        userName, password, SERVER_CLIENT_ID, SERVER_CLIENT_SECRET
                );

        try {
            Response<AccessToken> logInResponse = loginCall.execute();
            if (!logInResponse.isSuccess()) {
                reply.putExtra(RESULT_CODE, RC_ERROR);
                reply.putExtra(EXTRA_ERROR_MESSAGE, logInResponse.message());
            }
            else {

                String accessToken = logInResponse.body().getAccessToken();
                dmService = DmService.createService(httpClient.clone(), accessToken);

                Call<Void> gcmTokenCall = dmService.sendGcmToken(getGcmToken());
                Response<Void> gcmTokenResponse = gcmTokenCall.execute();

                if (!gcmTokenResponse.isSuccess()) {
                    reply.putExtra(RESULT_CODE, RC_ERROR);
                    reply.putExtra(EXTRA_ERROR_MESSAGE, gcmTokenResponse.message());
                }
                else {
                    reply.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
                    reply.putExtra(RESULT_CODE, RC_OK);
                }

            }
        } catch (IOException e) {
            reply.putExtra(RESULT_CODE, RC_ERROR);
            reply.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
        }

        mBroadcastManager.sendBroadcast(reply);
    }

    private void handleLogOut(Intent callerIntent) {

        Intent reply = new Intent(ACTION_LOG_OUT);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        Call<Void> call = dmService.logout();

        try {
            Response<Void> response = call.execute();
            if (!response.isSuccess()) {
                reply.putExtra(RESULT_CODE, RC_ERROR);
                reply.putExtra(EXTRA_ERROR_MESSAGE, response.message());
            }
            else {
                reply.putExtra(RESULT_CODE, RC_OK);
            }
        }
        catch (Exception e) {
            reply.putExtra(RESULT_CODE, RC_ERROR);
            reply.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
        }

        mBroadcastManager.sendBroadcast(reply);
    }

    private void handleSignUp(Intent callerIntent) {

        Intent reply = new Intent(ACTION_SIGN_UP);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        UserInfo signUpInfo = callerIntent.getParcelableExtra(EXTRA_USER_INFO);
        Call<Void> call = dmServiceUnsecured.signUp(signUpInfo);

        try {
            Response<Void> response = call.execute();
            if (!response.isSuccess()) {
                reply.putExtra(RESULT_CODE, RC_ERROR);
                reply.putExtra(EXTRA_ERROR_MESSAGE, response.message());
            }
            else {
                reply.putExtra(RESULT_CODE, RC_OK);
            }
        } catch (IOException e) {
            reply.putExtra(RESULT_CODE, RC_ERROR);
            reply.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
        }

        mBroadcastManager.sendBroadcast(reply);
    }

    private String getGcmToken() throws IOException {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        return token;
    }

}
