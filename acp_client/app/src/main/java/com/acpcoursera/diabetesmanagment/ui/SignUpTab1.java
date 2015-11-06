package com.acpcoursera.diabetesmanagment.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserInfo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class SignUpTab1 extends Fragment {

    private static String TAG = SignUpTab1.class.getSimpleName();

    private FragmentTabHost mTabHost;

    private ToggleButton mTeenButton;
    private ToggleButton mFollowerButton;

    private EditText mFirstNameEditText;
    private EditText mSecondNameEditText;

    private EditText mBirthDateEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_sign_up_tab_1, container, false);

        mTabHost = ((SignUpFragment) getParentFragment()).getTabHost();

        // next button
        Button nextButton = (Button) rootView.findViewById(R.id.sign_up_tab_1_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    updateSignUpInfo();
                    mTabHost.setCurrentTab(mTabHost.getCurrentTab() + 1);
                }
            }
        });

        // Teen vs Follower
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

        // first and second names
        mFirstNameEditText = (EditText) rootView.findViewById(R.id.sign_up_tab_1_first_name_edit_text);
        mSecondNameEditText = (EditText) rootView.findViewById(R.id.sign_up_tab_1_second_name_edit_text);

        // date of birth handling
        mBirthDateEditText = (EditText) rootView.findViewById(R.id.sign_up_tab_1_birth_date_edit_text);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // update birth date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                mBirthDateEditText.setText(dateFormat.format(myCalendar.getTime()));

            }
        };

        mBirthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        return rootView;
    }

    private boolean isInputValid() {
        boolean isValid  = true;
        if (TextUtils.isEmpty(mFirstNameEditText.getText().toString().trim())) {
            showToast(getActivity(), R.string.enter_first_name);
            isValid = false;
        }
        else if (TextUtils.isEmpty(mSecondNameEditText.getText().toString().trim())) {
            showToast(getActivity(), R.string.enter_second_name);
            isValid = false;
        }
        else if (TextUtils.isEmpty(mBirthDateEditText.getText().toString().trim())) {
            showToast(getActivity(), R.string.enter_birth_date);
            isValid = false;
        }
        return isValid;
    }

    private void updateSignUpInfo() {
        UserInfo signUpInfo = ((SignUpFragment) getParentFragment()).getSignUpInfo();
        signUpInfo.setUserType(mTeenButton.isChecked() ? UserInfo.TYPE_TEEN : UserInfo.TYPE_FOLLOWER);
        signUpInfo.setFirstName(mFirstNameEditText.getText().toString().trim());
        signUpInfo.setSecondName(mSecondNameEditText.getText().toString().trim());
        signUpInfo.setBirthDate(Date.valueOf(mBirthDateEditText.getText().toString().trim()));
    }

}
