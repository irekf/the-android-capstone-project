package com.acpcoursera.diabetesmanagment.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserInfo;

public class SignUpTab2 extends Fragment {

    private static String TAG = SignUpTab2.class.getSimpleName();

    private FragmentTabHost mTabHost;

    private EditText mMedicalRecordNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_sign_up_tab_2, container, false);

        mTabHost = ((SignUpFragment) getParentFragment()).getTabHost();

        // next and back buttons
        Button nextButton = (Button) rootView.findViewById(R.id.sign_up_tab_2_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    updateSignUpInfo();
                    mTabHost.setCurrentTab(mTabHost.getCurrentTab() + 1);
                }
            }
        });

        Button backButton = (Button) rootView.findViewById(R.id.sign_up_tab_2_back_button);
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

        // medical record number
        mMedicalRecordNumber = (EditText) rootView.findViewById(R.id.sign_up_tab_2_mrn_edit_text);

        return rootView;
    }

    private boolean isInputValid() {
        if (TextUtils.isEmpty(mMedicalRecordNumber.getText().toString().trim())) {
            Toast.makeText(getActivity(), R.string.enter_mrn, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void updateSignUpInfo() {
        UserInfo signUpInfo = ((SignUpFragment) getParentFragment()).getSignUpInfo();
        signUpInfo.setMedicalRecordNumber(mMedicalRecordNumber.getText().toString().trim());
    }

}
