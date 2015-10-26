package com.acpcoursera.diabetesmanagment.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserInfo;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;

public class SignUpTab3 extends Fragment {

    private static String TAG = SignUpTab3.class.getSimpleName();

    private FragmentTabHost mTabHost;

    private EditText mUserName;
    private EditText mPassword;
    private EditText mPassword2;
    private EditText mEmail;

    private NetOpsReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_sign_up_tab_3, container, false);

        mTabHost = ((SignUpFragment) getParentFragment()).getTabHost();

        // back and finish buttons
        Button finishButton = (Button) rootView.findViewById(R.id.sign_up_tab_3_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    updateSignUpInfo();
                    submitSignUpInfo();
                }
            }
        });

        Button backButton = (Button) rootView.findViewById(R.id.sign_up_tab_3_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabHost.setCurrentTab(mTabHost.getCurrentTab() - 1);
            }
        });

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    mTabHost.setCurrentTab(mTabHost.getCurrentTab() - 1);
                    return true;
                }
                return false;
            }
        });

        // username, password and e-mail
        mUserName = (EditText) rootView.findViewById(R.id.sign_up_tab_3_username_edit_text);
        mPassword = (EditText) rootView.findViewById(R.id.sign_up_tab_3_passwd1_edit_text);
        mPassword2 = (EditText) rootView.findViewById(R.id.sign_up_tab_3_passwd2_edit_text);
        mEmail = (EditText) rootView.findViewById(R.id.sign_up_tab_3_email_edit_text);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_SIGN_UP);
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
            if (action.equals(NetOpsService.ACTION_SIGN_UP)) {

                ProgressDialogFragment.dismiss(getActivity());

                if (resultCode == NetOpsService.RC_OK) {
                    Toast.makeText(getActivity(), R.string.account_created, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), AuthActivity.class));
                }
                else {
                    Toast.makeText(
                            getActivity(),
                            getActivity().getString(R.string.sign_up_error) +
                                    intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

        }

    }

    private boolean isInputValid() {
        boolean isValid  = true;
        if (TextUtils.isEmpty(mUserName.getText().toString().trim())) {
            Toast.makeText(getActivity(), R.string.enter_username, Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        else if (TextUtils.isEmpty(mEmail.getText().toString().trim())) {
            Toast.makeText(getActivity(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        else {
            String pwd1 = mPassword.getText().toString();
            String pwd2 = mPassword2.getText().toString();
            if (pwd1.isEmpty()) {
                Toast.makeText(getActivity(), R.string.enter_password, Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            else if (pwd2.isEmpty()) {
                Toast.makeText(getActivity(), R.string.enter_password2, Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            else if (!pwd1.equals(pwd2)) {
                Toast.makeText(getActivity(), R.string.passwords_incorrect, Toast.LENGTH_SHORT)
                        .show();
                mPassword.setText("");
                mPassword.requestFocus();
                mPassword2.setText("");
                isValid = false;
            }
        }
        return isValid;
    }

    private void updateSignUpInfo() {
        UserInfo signUpInfo = ((SignUpFragment) getParentFragment()).getSignUpInfo();
        signUpInfo.setUsername(mUserName.getText().toString().trim());
        signUpInfo.setPassword(mPassword.getText().toString().trim());
        signUpInfo.setEmail(mEmail.getText().toString().trim());
    }

    private void submitSignUpInfo() {
        UserInfo signUpInfo = ((SignUpFragment) getParentFragment()).getSignUpInfo();

        Intent intent = new Intent(getActivity(), NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_SIGN_UP);
        intent.putExtra(NetOpsService.EXTRA_USER_INFO, signUpInfo);
        getActivity().startService(intent);

        ProgressDialogFragment.show(getActivity());

        Toast.makeText(
                getActivity(),
                "Data submitted: " + signUpInfo.toString(),
                Toast.LENGTH_SHORT
        ).show();
    }

}
