package com.acpcoursera.diabetesmanagment.ui;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

public class LogInFragment extends Fragment {

    private static String TAG = LogInFragment.class.getSimpleName();

    private Callbacks mCallbacks;

    public interface Callbacks {
        public void onSignUpButtonClicked();
    }

    private Button mLogInButton;
    private Button mSignUpButton;

    private EditText mUserName;
    private EditText mPassword;

    private NetOpsReceiver mReceiver;

    private boolean mUiEnabled = true;
    private static String UI_ENABLED_KEY = "ui_enabled_key";

    @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCallbacks = (Callbacks) getActivity();

        mUserName = (EditText) getActivity().findViewById(R.id.user_name_edit_text);
        mPassword = (EditText) getActivity().findViewById(R.id.password_edit_text);

        mSignUpButton = (Button) getActivity().findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onSignUpButtonClicked();
            }
        });

        mLogInButton = (Button) getActivity().findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areCredentialsValid()) {
                    return;
                }
                setUiEnabled(false);
                // let's try to log in, i.e. get an access token
                Intent intent = new Intent(getActivity(), NetOpsService.class);
                intent.setAction(NetOpsService.ACTION_LOG_IN);
                intent.putExtra(NetOpsService.EXTRA_USER_NAME, mUserName.getText().toString());
                intent.putExtra(NetOpsService.EXTRA_PASSWORD, mPassword.getText().toString());
                getActivity().startService(intent);
            }
        });

        // check if UI is greyed out because logging in is in progress
        if (savedInstanceState != null) {
            mUiEnabled = savedInstanceState.getBoolean(UI_ENABLED_KEY);
            setUiEnabled(mUiEnabled);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(UI_ENABLED_KEY, mUiEnabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_LOG_IN);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_LOG_IN)) {
                if (resultCode == NetOpsService.RC_OK) {
                    // save the access token and go to the main screen
                    String accessToken = intent.getStringExtra(NetOpsService.EXTRA_ACCESS_TOKEN);
                    MiscUtils.saveAccessToken(getActivity(), accessToken);
                    Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(mainActivityIntent);
                }
                else {
                    setUiEnabled(true);
                    Toast.makeText(
                            getActivity(),
                            getActivity().getString(R.string.login_error) +
                                    intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

        }
    }

    private boolean areCredentialsValid() {
        if (mUserName.getText().toString().isEmpty()) {
            Toast.makeText(
                    getActivity(),
                    R.string.enter_username,
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        else if (mPassword.getText().toString().isEmpty()) {
            Toast.makeText(
                    getActivity(),
                    R.string.enter_password,
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        return true;
    }

    private void setUiEnabled(boolean enabled) {
        mUiEnabled = enabled;
        mLogInButton.setClickable(enabled);
        mSignUpButton.setClickable(enabled);
        mUserName.setEnabled(enabled);
        mPassword.setEnabled(enabled);
        int textColor = enabled ? Color.BLACK : Color.GRAY;
        mLogInButton.setTextColor(textColor);
        mSignUpButton.setTextColor(textColor);
    }

}
