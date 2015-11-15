package com.acpcoursera.diabetesmanagment.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.config.AcpPreferences;
import com.acpcoursera.diabetesmanagment.model.AccessToken;
import com.acpcoursera.diabetesmanagment.model.CheckInData;
import com.acpcoursera.diabetesmanagment.model.DmService;
import com.acpcoursera.diabetesmanagment.model.DmServiceProxy;
import com.acpcoursera.diabetesmanagment.model.UserInfo;
import com.acpcoursera.diabetesmanagment.model.UserSettings;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;
import com.acpcoursera.diabetesmanagment.util.NetUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_CLIENT_ID;
import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SERVER_CLIENT_SECRET;

public class NetOpsService extends IntentService {

    private static String TAG = NetOpsService.class.getSimpleName();

    public static String ACTION_SIGN_UP = "action_sign_up";
    public static String ACTION_LOG_IN = "action_log_in";
    public static String ACTION_LOG_OUT = "action_log_out";

    public static String ACTION_CHECK_IN = "action_check_in";
    public static String ACTION_GET_USER_LIST = "action_get_user_list";
    public static String ACTION_FOLLOW = "action_follow";
    public static String ACTION_INVITE = "action_invite";
    public static String ACTION_ACCEPT = "action_accept";
    public static String ACTION_DELETE = "action_delete";
    public static String ACTION_CHANGE_SETTINGS = "action_change_setting";

    public static String ARG_USER_NAME = "user_name";
    public static String ARG_PASSWORD = "password";
    public static String ARG_TEEN_ONLY = "teen_only";
    public static String ARG_USER_SETTINGS = "user_settings";
    public static String ARG_IS_INVITE = "is_invite";
    public static String ARG_IS_FOLLOWER = "is_follower";

    public static String EXTRA_USER_INFO = "user_info";
    public static String EXTRA_CHECK_IN_DATA = "check_in_data";
    public static String EXTRA_USER_LIST = "user_list";
    public static String EXTRA_ERROR_MESSAGE = "error_message";

    public static String RESULT_CODE = "result_code";
    public static int RC_OK = 0;
    public static int RC_ERROR = 8;
    public static int RC_MISSING = 16;

    private LocalBroadcastManager mBroadcastManager;

    public static OkHttpClient mHttpClient;
    private static DmServiceProxy sDmServiceUnsecured;
    private static DmServiceProxy sDmService;

    public NetOpsService() {
        super("NetOpsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mHttpClient == null) {
            try {
                mHttpClient = NetUtils.getSecureHttpClient(getApplicationContext());
            } catch (NetUtils.SecureHttpClientException e) {
                e.printStackTrace();
            }
        }

        if (sDmServiceUnsecured == null) {
            sDmServiceUnsecured = DmService.createService(mHttpClient.clone());
        }

        if (sDmService == null) {
            String accessToken = MiscUtils.readAccessToken(getApplicationContext());
            if (accessToken != null) {
                sDmService = DmService.createService(mHttpClient.clone(), accessToken);
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
        if (mHttpClient != null) {
            if (action.equals(ACTION_LOG_IN)) {
                handleLogIn(intent);
            }
            else if (action.equals(ACTION_LOG_OUT)) {
                handleLogOut(intent);
            }
            else if (action.equals(ACTION_SIGN_UP)) {
                handleSignUp(intent);
            }
            else if (action.equals(ACTION_CHECK_IN)) {
                handleCheckIn(intent);
            }
            else if (action.equals(ACTION_GET_USER_LIST)) {
                handleGetUserList(intent);
            }
            else if (action.equals(ACTION_FOLLOW)) {
                handleFollowRequest(intent);
            }
            else if (action.equals(ACTION_INVITE)) {
                handleInviteRequest(intent);
            }
            else if (action.equals(ACTION_ACCEPT)) {
                handleAcceptRequest(intent);
            }
            else if (action.equals(ACTION_DELETE)) {
                handleDeleteRequest(intent);
            }
            else if (action.equals(ACTION_CHANGE_SETTINGS)) {
                handleChangeSettingsRequest(intent);
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

        String userName = callerIntent.getStringExtra(ARG_USER_NAME);
        String password = callerIntent.getStringExtra(ARG_PASSWORD);

        Call<AccessToken> loginCall =
                sDmServiceUnsecured.login(
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
                sDmService = DmService.createService(mHttpClient.clone(), accessToken);

                Call<Void> gcmTokenCall = sDmService.sendGcmToken(getGcmToken());
                Response<Void> gcmTokenResponse = gcmTokenCall.execute();

                if (!gcmTokenResponse.isSuccess()) {
                    reply.putExtra(RESULT_CODE, RC_ERROR);
                    reply.putExtra(EXTRA_ERROR_MESSAGE, gcmTokenResponse.message());
                }
                else {
                    MiscUtils.saveAccessToken(getApplicationContext(), accessToken);
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

        Call<Void> call = sDmService.logout();

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

        // clear the access token and null the DM service
        MiscUtils.saveAccessToken(getApplicationContext(), "");
        sDmService = null;

        mBroadcastManager.sendBroadcast(reply);
    }

    private void handleSignUp(Intent callerIntent) {

        Intent reply = new Intent(ACTION_SIGN_UP);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        UserInfo signUpInfo = callerIntent.getParcelableExtra(EXTRA_USER_INFO);
        Call<Void> call = sDmServiceUnsecured.signUp(signUpInfo);

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

    private void handleCheckIn(Intent callerIntent) {

        Intent reply = new Intent(ACTION_CHECK_IN);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        CheckInData checkInData = callerIntent.getParcelableExtra(EXTRA_CHECK_IN_DATA);
        Call<Void> call = sDmService.checkIn(checkInData);

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

    private void handleGetUserList(Intent callerIntent) {

        Intent reply = new Intent(ACTION_GET_USER_LIST);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        boolean teenOnly = callerIntent.getBooleanExtra(ARG_TEEN_ONLY, false);

        Call<List<UserInfo>> call = sDmService.getUserList(teenOnly);

        try {
            Response<List<UserInfo>> response = call.execute();
            if (!response.isSuccess()) {
                reply.putExtra(RESULT_CODE, RC_ERROR);
                reply.putExtra(EXTRA_ERROR_MESSAGE, response.message());
            }
            else {
                List<UserInfo> users = response.body();
                reply.putExtra(EXTRA_USER_LIST, users.toArray(new UserInfo[users.size()]));
                reply.putExtra(RESULT_CODE, RC_OK);
            }
        } catch (IOException e) {
            reply.putExtra(RESULT_CODE, RC_ERROR);
            reply.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
        }

        mBroadcastManager.sendBroadcast(reply);
    }

    private void handleFollowRequest(Intent callerIntent) {

        Intent reply = new Intent(ACTION_FOLLOW);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        String usernameToFollow = callerIntent.getStringExtra(ARG_USER_NAME);
        UserSettings settings = callerIntent.getParcelableExtra(ARG_USER_SETTINGS);

        Call<Void> call = sDmService.follow(usernameToFollow,
                settings.isMajorData(), settings.isMinorData());

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

    private void handleInviteRequest(Intent callerIntent) {

        Intent reply = new Intent(ACTION_INVITE);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        String usernameToInvite = callerIntent.getStringExtra(ARG_USER_NAME);
        UserSettings settings = callerIntent.getParcelableExtra(ARG_USER_SETTINGS);

        Call<Void> call = sDmService.invite(usernameToInvite,
                settings.isMajorData(), settings.isMinorData());

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

    private void handleAcceptRequest(Intent callerIntent) {

        Intent reply = new Intent(ACTION_ACCEPT);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        String usernameToAccept = callerIntent.getStringExtra(ARG_USER_NAME);
        boolean isInvite = callerIntent.getBooleanExtra(ARG_IS_INVITE, false);
        UserSettings settings = callerIntent.getParcelableExtra(ARG_USER_SETTINGS);

        Call<Void> call = sDmService.accept(usernameToAccept, isInvite,
                settings.isMajorData(), settings.isMinorData());

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

    private void handleDeleteRequest(Intent callerIntent) {

        Intent reply = new Intent(ACTION_ACCEPT);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        String username = callerIntent.getStringExtra(ARG_USER_NAME);
        boolean isFollower = callerIntent.getBooleanExtra(ARG_IS_FOLLOWER, false);

        Call<Void> call = sDmService.delete(username, isFollower);

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

    private void handleChangeSettingsRequest(Intent callerIntent) {

        Intent reply = new Intent(ACTION_ACCEPT);
        reply.addCategory(Intent.CATEGORY_DEFAULT);

        String usernameToDelete = callerIntent.getStringExtra(ARG_USER_NAME);
        UserSettings settings = callerIntent.getParcelableExtra(ARG_USER_SETTINGS);

        Call<Void> call = sDmService.changeSettings(usernameToDelete,
                settings.isMajorData(), settings.isMinorData());

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
