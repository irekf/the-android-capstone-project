package com.acpcoursera.diabetesmanagment.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.acpcoursera.diabetesmanagment.R;

public class SignUpTab3 extends Fragment {

    private static String TAG = SignUpTab3.class.getSimpleName();

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_sign_up_tab_3, container, false);

        mTabHost = ((SignUpFragment) getParentFragment()).getTabHost();

        Button finishButton = (Button) rootView.findViewById(R.id.sign_up_tab_3_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    Toast.makeText(
                            getActivity(),
                            "Data submitted (not really)",
                            Toast.LENGTH_SHORT
                    ).show();
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

        return rootView;
    }

    private boolean isInputValid() {
        return true;
    }

}
