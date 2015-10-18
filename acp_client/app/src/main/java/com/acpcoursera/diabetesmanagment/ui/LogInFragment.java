package com.acpcoursera.diabetesmanagment.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;

public class LogInFragment extends Fragment {

    private static String TAG = LogInFragment.class.getSimpleName();

    Callbacks mCallbacks;

    public interface Callbacks {
        public void onSignUpButtonClicked();
    }

    Button mLogInButton;
    Button mSignUpButton;

    EditText mUserName;
    EditText mPassword;

    NetOpsReceiver mReceiver;

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
                // let's try to log in, i.e. get an access token
                Intent intent = new Intent(getActivity(), NetOpsService.class);
                intent.setAction(NetOpsService.ACTION_LOG_IN);
                intent.putExtra(NetOpsService.EXTRA_USER_NAME, mUserName.getText().toString());
                intent.putExtra(NetOpsService.EXTRA_PASSWORD, mPassword.getText().toString());
                getActivity().startService(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_LOG_IN);
        filter.addAction(NetOpsService.ACTION_SIGN_UP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    public class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "intent received");

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_LOG_IN)) {
                if (resultCode == NetOpsService.RC_OK) {
                    String accessToken = intent.getStringExtra(NetOpsService.EXTRA_ACCESS_TOKEN);
                    Toast.makeText(
                            getActivity(),
                            "Access token: " + accessToken,
                            Toast.LENGTH_SHORT
                    ).show();
                }
                else {
                    Toast.makeText(
                            getActivity(),
                            "Login error: " + intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
            else if (action.equals(NetOpsService.ACTION_SIGN_UP)) {

            }
            else {

            }

        }
    }

}
