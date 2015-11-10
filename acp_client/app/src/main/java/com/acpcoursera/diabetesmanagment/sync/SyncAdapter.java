package com.acpcoursera.diabetesmanagment.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.acpcoursera.diabetesmanagment.model.CheckInData;
import com.acpcoursera.diabetesmanagment.model.DmService;
import com.acpcoursera.diabetesmanagment.model.DmServiceProxy;
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
                        /* TODO this should go to a helper method #2 */
                        values[i] = new ContentValues();
                        values[i].put(DmContract.CheckInData.USERNAME, checkInData.get(i).getUsername());
                        values[i].put(DmContract.CheckInData.SUGAR_LEVEL, checkInData.get(i).getSugarLevel());
                        values[i].put(DmContract.CheckInData.SUGAR_LEVEL_TIME, checkInData.get(i).getSugarLevelTime().toString());
                        values[i].put(DmContract.CheckInData.MEAL, checkInData.get(i).getMeal());
                        values[i].put(DmContract.CheckInData.MEAL_TIME, checkInData.get(i).getMealTime().toString());
                        values[i].put(DmContract.CheckInData.INSULIN_DOSAGE, checkInData.get(i).getInsulinDosage());
                        values[i].put(DmContract.CheckInData.INSULIN_ADMINISTRATION_TIME, checkInData.get(i).getInsulinAdministrationTime().toString());
                        values[i].put(DmContract.CheckInData.MOOD_LEVEL, checkInData.get(i).getMoodLevel());
                        values[i].put(DmContract.CheckInData.STRESS_LEVEL, checkInData.get(i).getStressLevel());
                        values[i].put(DmContract.CheckInData.ENERGY_LEVEL, checkInData.get(i).getEnergyLevel());
                        values[i].put(DmContract.CheckInData.CHECK_IN_TIME, checkInData.get(i).getCheckInTimestamp().toString());
                        /* TODO -------------------------------------- */
                    }

                    mContentResolver.delete(DmContract.CheckInData.buildCheckInDataUri(), null, null);
                    mContentResolver.bulkInsert(DmContract.CheckInData.buildCheckInDataUri(), values);

                    Cursor c = mContentResolver.query(DmContract.CheckInData.buildCheckInDataUri(), null, null, null, null);
                    if (c != null) {
                        try {
                            while (c.moveToNext()) {
                                Log.d(TAG, c.getString(4) + " " + c.getString(5));
                            }
                        } finally {
                            c.close();
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* TODO -------------------------------------- */
        }
        else {
            Log.w(TAG, "unknown table to sync: " + tableName);
        }

    }
}
