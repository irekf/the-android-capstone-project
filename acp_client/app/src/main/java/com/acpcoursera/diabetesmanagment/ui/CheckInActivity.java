package com.acpcoursera.diabetesmanagment.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.CheckInData;
import com.acpcoursera.diabetesmanagment.service.NetOpsService;
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.hideKeyboard;
import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class CheckInActivity extends AppCompatActivity {

    private static String TAG = CheckInActivity.class.getSimpleName();

    private static final String MOOD_LEVEL_KEY = "mood_level_key";
    private static final String STRESS_LEVEL_KEY = "stress_level_key";
    private static final String ENERGY_LEVEL_KEY = "energy_level_key";

    private NumberPicker mMoodNumberPicker;
    private NumberPicker mStressNumberPicker;
    private NumberPicker mEnergyNumberPicker;

    private EditText mSugarLevel;
    private EditText mMeal;
    private EditText mInsulinDosage;

    private EditText mMeasurementTime;
    private EditText mMealTime;
    private EditText mInsulinAdministrationTime;
    private EditText mCurrentTimeAndDateField;

    private Button mSubmitButton;

    private NetOpsReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        EditText.OnClickListener numericFieldsListener = new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
                MiscUtils.showKeyboard(CheckInActivity.this, v);
            }
        };

        mSugarLevel = (EditText) findViewById(R.id.sugar_level_edit_text);
        mSugarLevel.setOnClickListener(numericFieldsListener);
        mMeal = (EditText) findViewById(R.id.meal_edit_text);
        mMeal.setOnClickListener(numericFieldsListener);
        mInsulinDosage = (EditText) findViewById(R.id.dosage_edit_text);
        mInsulinDosage.setOnClickListener(numericFieldsListener);

        mMoodNumberPicker = (NumberPicker) findViewById(R.id.mood_number_picker);
        mMoodNumberPicker.setMinValue(1);
        mMoodNumberPicker.setMaxValue(10);

        mStressNumberPicker = (NumberPicker) findViewById(R.id.stress_number_picker);
        mStressNumberPicker.setMinValue(1);
        mStressNumberPicker.setMaxValue(10);

        mEnergyNumberPicker = (NumberPicker) findViewById(R.id.energy_number_picker);
        mEnergyNumberPicker.setMinValue(1);
        mEnergyNumberPicker.setMaxValue(10);

        if (savedInstanceState != null) {
            int mood = savedInstanceState.getInt(MOOD_LEVEL_KEY);
            mMoodNumberPicker.setValue(mood);
            int stress = savedInstanceState.getInt(STRESS_LEVEL_KEY);
            mStressNumberPicker.setValue(stress);
            int energy = savedInstanceState.getInt(ENERGY_LEVEL_KEY);
            mEnergyNumberPicker.setValue(energy);
        }

        final Calendar myCalendar = Calendar.getInstance();
        final TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US);
                mCurrentTimeAndDateField.setText(dateFormat.format(myCalendar.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog dialog = new TimePickerDialog(CheckInActivity.this, timeListener,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true);
                dialog.setTitle((String) mCurrentTimeAndDateField.getTag());
                dialog.show();
            }
        };

        EditText.OnClickListener timeAndDateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentTimeAndDateField = (EditText) v;
                DatePickerDialog dialog = new DatePickerDialog(CheckInActivity.this, dateListener,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle((String) mCurrentTimeAndDateField.getTag());
                dialog.show();
            }
        };

        mMeasurementTime = (EditText) findViewById(R.id.measurement_time_edit_text);
        mMeasurementTime.setOnClickListener(timeAndDateListener);
        mMealTime = (EditText) findViewById(R.id.meal_time_edit_text);
        mMealTime.setOnClickListener(timeAndDateListener);
        mInsulinAdministrationTime = (EditText) findViewById(R.id.insulin_administered_edit_text);
        mInsulinAdministrationTime.setOnClickListener(timeAndDateListener);

        mSubmitButton = (Button) findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    hideKeyboard(CheckInActivity.this, CheckInActivity.this.getCurrentFocus());
                    ProgressDialogFragment.show(CheckInActivity.this);
                    Intent intent = new Intent(CheckInActivity.this, NetOpsService.class);
                    intent.setAction(NetOpsService.ACTION_CHECK_IN);
                    intent.putExtra(NetOpsService.EXTRA_CHECK_IN_DATA, collectCheckInData());
                    startService(intent);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_CHECK_IN);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_CHECK_IN)) {
                if (resultCode == NetOpsService.RC_OK) {
                    showToast(CheckInActivity.this, getString(R.string.check_in_submitted));
                }
                else {
                    showToast(CheckInActivity.this, getString(R.string.check_in_error) +
                            intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE));
                }
                ProgressDialogFragment.dismiss(CheckInActivity.this);
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MOOD_LEVEL_KEY, mMoodNumberPicker.getValue());
        outState.putInt(STRESS_LEVEL_KEY, mStressNumberPicker.getValue());
        outState.putInt(ENERGY_LEVEL_KEY, mEnergyNumberPicker.getValue());
    }

    private boolean isInputValid() {
        boolean isValid  = true;
        EditText viewToFocus = null;
        if (mSugarLevel.getText().toString().isEmpty()) {
            viewToFocus = mSugarLevel;
        }
        else if (mMeasurementTime.getText().toString().isEmpty()) {
            viewToFocus = mMeasurementTime;
        }
        else if (mMeal.getText().toString().isEmpty()) {
            viewToFocus = mMeal;
        }
        else if (mMealTime.getText().toString().isEmpty()) {
            viewToFocus = mMealTime;
        }
        else if (mInsulinAdministrationTime.getText().toString().isEmpty()) {
            viewToFocus = mInsulinAdministrationTime;
        }
        else if (mInsulinDosage.getText().toString().isEmpty()) {
            viewToFocus = mInsulinDosage;
        }

        if (viewToFocus != null) {
            MiscUtils.showToast(this, getString(R.string.error_need_required_data));
            viewToFocus.performClick();
            isValid = false;
        }

        return isValid;
    }

    private CheckInData collectCheckInData() {
        CheckInData data = new CheckInData();
        data.setSugarLevel(Float.valueOf(mSugarLevel.getText().toString()));
        data.setSugarLevelTime(mMeasurementTime.getText().toString());
        data.setMeal(mMeal.getText().toString());
        data.setMealTime(mMealTime.getText().toString());
        data.setInsulinDosage(Float.valueOf(mInsulinDosage.getText().toString()));
        data.setInsulinAdministrationTime(mInsulinAdministrationTime.getText().toString());
        data.setMoodLevel(mMoodNumberPicker.getValue());
        data.setStressLevel(mStressNumberPicker.getValue());
        data.setEnergyLevel(mEnergyNumberPicker.getValue());
        data.setCheckInTimestamp(DateFormat.getDateTimeInstance().format(new Date()));
        return data;
    }

}
