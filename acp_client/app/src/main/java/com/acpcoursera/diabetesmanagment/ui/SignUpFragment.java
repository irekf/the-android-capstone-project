package com.acpcoursera.diabetesmanagment.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserInfo;

public class SignUpFragment extends Fragment {

    private static String TAG = SignUpFragment.class.getSimpleName();

    private UserInfo signUpInfo;
    private static String SIGN_UP_INFO_KEY = "sign_up_info_key";

    private FragmentTabHost mTabHost;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            signUpInfo = savedInstanceState.getParcelable(SIGN_UP_INFO_KEY);
        }
        else {
            signUpInfo = new UserInfo();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.sign_up_tab_content);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.sign_up_tab_1)),
                SignUpTab1.class, null
        );
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.sign_up_tab_2)),
                SignUpTab2.class, null
        );
        mTabHost.addTab(
                mTabHost.newTabSpec("tab3").setIndicator(getString(R.string.sign_up_tab_3)),
                SignUpTab3.class, null
        );

        mTabHost.getTabWidget().setEnabled(false);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SIGN_UP_INFO_KEY, signUpInfo);
    }

    public UserInfo getSignUpInfo() {
        return signUpInfo;
    }

    public FragmentTabHost getTabHost() {
        return mTabHost;
    }

}
