package com.acpcoursera.diabetesmanagment.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
                Call<AccessToken> call =
                        dmServiceUnsecured.login(
                                Credentials.basic(SERVER_CLIENT_ID, SERVER_CLIENT_SECRET),
                                userName, password, SERVER_CLIENT_ID, SERVER_CLIENT_SECRET
                        );
                try {
                    Response<AccessToken> response = call.execute();
                    if (response.code() < 200 || response.code() > 299) {
                        broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
                        broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, response.message());
                    }
                    else {
                        String accessToken = response.body().getAccessToken();
                        dmService = DmService.createService(httpClient.clone(), accessToken);
                        broadcastIntent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
                        broadcastIntent.putExtra(RESULT_CODE, RC_OK);

                        // TODO remove this: test access token
                        Call<Void> call2 = dmService.sendGcmToken(getGcmToken());
                        try {
                            Response<Void> resp = call2.execute();
                            Log.d(TAG, "resp code: " + resp.code());
                        }
                        catch (Exception e) {
                            Log.d(TAG, "error: " + e.getMessage());
                        }


                    }
                } catch (IOException e) {
                    broadcastIntent.putExtra(RESULT_CODE, RC_ERROR);
                    broadcastIntent.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
                    e.printStackTrace();
                }
            }
            else if (action.equals(ACTION_SIGN_UP)) {
                UserInfo signUpInfo = intent.getParcelableExtra(EXTRA_USER_INFO);
                Call<Void> call = dmServiceUnsecured.signUp(signUpInfo);
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

    private String getGcmToken() throws IOException {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        return token;
    }

}
