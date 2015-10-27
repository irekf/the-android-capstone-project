package com.acpcoursera.diabetesmanagment.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.ACCESS_TOKEN_PREF;
import static com.acpcoursera.diabetesmanagment.config.AcpPreferences.SHARED_PREF_FILE;

public class MiscUtils {

    private static String TAG = MiscUtils.class.getSimpleName();

    public static void saveAccessToken(Context context, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN_PREF, token);
        editor.commit();
    }

    public static String readAccessToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(ACCESS_TOKEN_PREF, "");
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
