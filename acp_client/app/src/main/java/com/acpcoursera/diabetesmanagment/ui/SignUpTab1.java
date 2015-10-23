package com.acpcoursera.diabetesmanagment.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.acpcoursera.diabetesmanagment.R;

public class SignUpTab1 extends Fragment {

    private static String TAG = SignUpTab1.class.getSimpleName();

    private FragmentTabHost mTabHost;
    private ToggleButton mTeenButton;
    private ToggleButton mFollowerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_sign_up_tab_1, container, false);

        mTabHost = ((SignUpFragment) getParentFragment()).getTabHost();

        Button nextButton = (Button) rootView.findViewById(R.id.sign_up_tab_1_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    mTabHost.setCurrentTab(mTabHost.getCurrentTab() + 1);
                }
            }
        });

        mTeenButton = (ToggleButton) rootView.findViewById(R.id.sign_up_tab_1_teen_button);
        mFollowerButton = (ToggleButton) rootView.findViewById(R.id.sign_up_tab_1_follower_button);

        mTeenButton.setChecked(true);

        mTeenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowerButton.setChecked(false);
                mTeenButton.setChecked(true);
            }
        });

        mFollowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowerButton.setChecked(true);
                mTeenButton.setChecked(false);
            }
        });

        return rootView;
    }

    private boolean isInputValid() {
        return true;
    }

}
