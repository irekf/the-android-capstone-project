package com.acpcoursera.diabetesmanagment.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.acpcoursera.diabetesmanagment.R;

public class LogInFragment extends Fragment {

    private static String TAG = LogInFragment.class.getSimpleName();

    Callbacks mCallbacks;

    public interface Callbacks {
        public void onSignUpButtonClicked();
    }

    Button mLogInButton;
    Button mSignUpButton;

    @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCallbacks = (Callbacks) getActivity();

        mSignUpButton = (Button) getActivity().findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onSignUpButtonClicked();
            }
        });

    }

}
