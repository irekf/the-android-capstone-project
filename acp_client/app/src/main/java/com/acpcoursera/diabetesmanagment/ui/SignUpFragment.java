package com.acpcoursera.diabetesmanagment.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acpcoursera.diabetesmanagment.R;

public class SignUpFragment extends Fragment {

    private static String TAG = SignUpFragment.class.getSimpleName();

    private FragmentTabHost mTabHost;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.sign_up_tab_content);

        mTabHost.addTab(mTabHost.newTabSpec("step1").setIndicator("STEP 1"),
                Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("step2").setIndicator("STEP 2"),
                Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("finish").setIndicator("FINISH"),
                Fragment.class, null);

        return rootView;
    }

}
