package com.acpcoursera.diabetesmanagment.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.acpcoursera.diabetesmanagment.model.CheckInData;
import com.acpcoursera.diabetesmanagment.model.DmService;
import com.acpcoursera.diabetesmanagment.model.DmServiceProxy;
import com.acpcoursera.diabetesmanagment.model.Follower;
import com.acpcoursera.diabetesmanagment.model.Following;
import com.acpcoursera.diabetesmanagment.provider.DmContract;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;
import com.acpcoursera.diabetesmanagment.util.NetUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String TAG = SyncAdapter.class.getSimpleName();

    private ContentResolver mContentResolver;

    private OkHttpClient mHttpClient;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();

        try {
            mHttpClient = NetUtils.getSecureHttpClient(getContext());
        } catch (NetUtils.SecureHttpClientException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        DmServiceProxy dmService;
        String accessToken = MiscUtils.readAccessToken(getContext());
        if (mHttpClient !=  null && accessToken != null) {
            dmService = DmService.createService(mHttpClient.clone(), accessToken);
        }
        else {
            String tokenAddress = (accessToken == null) ? "not null" : "null";
            Log.w(TAG, "Can't perform sync, http client = " + mHttpClient
                    + ", access token = " + tokenAddress);
            return;
        }

        String tableName = extras.getString("table");
        if (tableName == null) {
            Log.w(TAG, "no table to sync");
            return;
        }

        Log.d(TAG, "syncing table: " + tableName);

        if (tableName.equals(DmContract.CheckInData.CONTENT_TYPE_ID)) {
            /* TODO this should go to a helper method #1 */
            Call<List<CheckInData>> getCheckInDataCall = dmService.getCheckInData();
            try {
                Response<List<CheckInData>> response = getCheckInDataCall.execute();
                if (response.isSuccess()) {

                    List<CheckInData> checkInData = response.body();

                    ContentValues[] values = new ContentValues[checkInData.size()];
                    for (int i = 0; i < values.length; i++) {
                        values[i] = checkInData.get(i).getContentValues();
                    }

                    mContentResolver.delete(DmContract.CheckInData.buildCheckInDataUri(), null, null);
                    mContentResolver.bulkInsert(DmContract.CheckInData.buildCheckInDataUri(), values);
                }
                else {
                    Log.w(TAG, response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* TODO -------------------------------------- */
        }
        else if (tableName.equals(DmContract.Followers.CONTENT_TYPE_ID)) {
            Call<List<Follower>> getFollowers = dmService.getFollowers();
            try {
                Response<List<Follower>> response = getFollowers.execute();
                if (response.isSuccess()) {

                    List<Follower> followers = response.body();

                    ContentValues[] values = new ContentValues[followers.size()];
                    for (int i = 0; i < values.length; i++) {
                        values[i] = followers.get(i).getContentValues();
                        Log.d(TAG, followers.get(i).toString());
                    }

                    mContentResolver.delete(DmContract.Followers.buildFollowersUri(), null, null);
                    mContentResolver.bulkInsert(DmContract.Followers.buildFollowersUri(), values);
                }
                else {
                    Log.w(TAG, response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (tableName.equals(DmContract.Following.CONTENT_TYPE_ID)) {
            Call<List<Following>> getFollowing = dmService.getFollowing();
            try {
                Response<List<Following>> response = getFollowing.execute();
                if (response.isSuccess()) {

                    List<Following> following = response.body();

                    ContentValues[] values = new ContentValues[following.size()];
                    for (int i = 0; i < values.length; i++) {
                        values[i] = following.get(i).getContentValues();
                        Log.d(TAG, following.get(i).toString());
                    }

                    mContentResolver.delete(DmContract.Following.buildFollowingsUri(), null, null);
                    mContentResolver.bulkInsert(DmContract.Following.buildFollowingsUri(), values);
                }
                else {
                    Log.w(TAG, response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.w(TAG, "unknown table to sync: " + tableName);
        }

    }
}
