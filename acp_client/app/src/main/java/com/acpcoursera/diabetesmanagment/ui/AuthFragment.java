package com.acpcoursera.diabetesmanagment.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.acpcoursera.diabetesmanagment.R;

public class AuthFragment extends Fragment {

    private static String TAG = AuthFragment.class.getSimpleName();

    Callbacks mCallbacks;

    public interface Callbacks {
        public void onLogInButtonClicked();
        public void onSignUpButtonClicked();
    }

    Button mLogInButton;
    Button mSignUpButton;

    @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCallbacks = (Callbacks) getActivity();

        mLogInButton = (Button) getActivity().findViewById(R.id.log_in_button);
        mSignUpButton = (Button) getActivity().findViewById(R.id.sign_up_button);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onLogInButtonClicked();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onSignUpButtonClicked();
            }
        });

    }

}
