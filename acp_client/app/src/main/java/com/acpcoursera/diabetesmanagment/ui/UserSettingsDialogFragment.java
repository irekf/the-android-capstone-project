package com.acpcoursera.diabetesmanagment.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserSettings;

public class UserSettingsDialogFragment extends DialogFragment {

    public static final String TAG = UserSettingsDialogFragment.class.getSimpleName();

    public interface UserSettingsDialogListener {
        void onFinishUserSettingsDialog(UserSettings setting);
    }

    private static final String USER_SETTINGS_ARGS_KEY = "user_settings_args_key";
    private static final String USER_SETTINGS_KEY = "user_settings_key";

    private UserSettings mUserSettings;

    public UserSettingsDialogFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mUserSettings = savedInstanceState.getParcelable(USER_SETTINGS_KEY);
        }
        else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mUserSettings = arguments.getParcelable(USER_SETTINGS_ARGS_KEY);
            }
            if (mUserSettings == null) {
                mUserSettings = new UserSettings(false, false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(USER_SETTINGS_KEY, mUserSettings);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_user_settings, null);
        CheckBox majorData = (CheckBox) rootView.findViewById(R.id.major_data_checkbox);
        CheckBox minorData = (CheckBox) rootView.findViewById(R.id.minor_data_checkbox);

        majorData.setOnCheckedChangeListener(null);
        minorData.setOnCheckedChangeListener(null);
        if (mUserSettings != null) {
            majorData.setChecked(mUserSettings.isMajorData());
            minorData.setChecked(mUserSettings.isMinorData());
        }

        majorData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserSettings.setMajorData(isChecked);
            }
        });

        minorData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserSettings.setMinorData(isChecked);
            }
        });

        final UserSettingsDialogListener activity = (UserSettingsDialogListener) getActivity();

        builder.setMessage(getString(R.string.user_settings_label))
                .setView(rootView)
                .setPositiveButton(getString(R.string.label_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activity.onFinishUserSettingsDialog(mUserSettings);
                            }
                        })
                .setNegativeButton(getString(R.string.label_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UserSettingsDialogFragment.this.dismiss();
                            }
                        });

        return builder.create();
    }

}
