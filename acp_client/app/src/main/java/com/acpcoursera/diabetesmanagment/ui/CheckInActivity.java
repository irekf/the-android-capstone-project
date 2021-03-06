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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.hideKeyboard;
import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class CheckInActivity extends AppCompatActivity {

    private static String TAG = CheckInActivity.class.getSimpleName();

    private static final String MOOD_LEVEL_KEY = "mood_level_key";
    private static final String STRESS_LEVEL_KEY = "stress_level_key";
    private static final String ENERGY_LEVEL_KEY = "energy_level_key";
    private static final String SUGAR_TIME_CALENDAR_KEY = "sugar_time_calendar_key";
    private static final String MEAL_TIME_CALENDAR_KEY = "meal_time_calendar_key";
    private static final String INSULIN_TIME_CALENDAR_KEY = "insulin_time_calendar_key";

    private NumberPicker mMoodNumberPicker;
    private NumberPicker mStressNumberPicker;
    private NumberPicker mEnergyNumberPicker;

    private EditText mSugarLevel;
    private EditText mMeal;
    private EditText mInsulinDosage;

    private EditText mSugarTime;
    private EditText mMealTime;
    private EditText mInsulinTime;
    private Calendar mSugarTimeCalendar;
    private Calendar mMealTimeCalendar;
    private Calendar mInsulinTimeCalendar;
    private EditText mSugarLevelWho;
    private EditText mSugarLevelWhere;
    private EditText mFeelings;

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

            mSugarTimeCalendar = (Calendar) savedInstanceState
                    .getSerializable(SUGAR_TIME_CALENDAR_KEY);
            mMealTimeCalendar = (Calendar) savedInstanceState
                    .getSerializable(MEAL_TIME_CALENDAR_KEY);
            mInsulinTimeCalendar = (Calendar) savedInstanceState
                    .getSerializable(INSULIN_TIME_CALENDAR_KEY);
        }
        else {
            mSugarTimeCalendar = Calendar.getInstance();
            mMealTimeCalendar = Calendar.getInstance();
            mInsulinTimeCalendar = Calendar.getInstance();
        }

        mSugarTime = (EditText) findViewById(R.id.measurement_time_edit_text);
        mSugarTime.setOnClickListener(new TimeAndDateListener(mSugarTimeCalendar));
        mMealTime = (EditText) findViewById(R.id.meal_time_edit_text);
        mMealTime.setOnClickListener(new TimeAndDateListener(mMealTimeCalendar));
        mInsulinTime = (EditText) findViewById(R.id.insulin_administered_edit_text);
        mInsulinTime.setOnClickListener(new TimeAndDateListener(mInsulinTimeCalendar));
        mSugarLevelWho = (EditText) findViewById(R.id.who_edit_text);
        mSugarLevelWhere = (EditText) findViewById(R.id.where_edit_text);
        mFeelings = (EditText) findViewById(R.id.feelings_edit_text);

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

    private class TimeAndDateListener implements
            TimePickerDialog.OnTimeSetListener,
            DatePickerDialog.OnDateSetListener,
            View.OnClickListener {

        private Calendar mCalendar;
        private EditText mCurrentTimeAndDateField;

        public TimeAndDateListener(Calendar calendar) {
            mCalendar = calendar;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            mCurrentTimeAndDateField.setText(dateFormat.format(mCalendar.getTime()));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            TimePickerDialog dialog = new TimePickerDialog(CheckInActivity.this, this,
                    mCalendar.get(Calendar.HOUR_OF_DAY),
                    mCalendar.get(Calendar.MINUTE), true);
            dialog.setTitle((String) mCurrentTimeAndDateField.getTag());
            dialog.show();
        }

        @Override
        public void onClick(View v) {
            mCurrentTimeAndDateField = (EditText) v;
            DatePickerDialog dialog = new DatePickerDialog(CheckInActivity.this, this,
                    mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.setTitle((String) mCurrentTimeAndDateField.getTag());
            dialog.show();
        }
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
                    finish();
                }
                else {
                    ProgressDialogFragment.dismiss(CheckInActivity.this);
                    showToast(CheckInActivity.this, getString(R.string.check_in_error) +
                            intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE));
                }
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MOOD_LEVEL_KEY, mMoodNumberPicker.getValue());
        outState.putInt(STRESS_LEVEL_KEY, mStressNumberPicker.getValue());
        outState.putInt(ENERGY_LEVEL_KEY, mEnergyNumberPicker.getValue());

        outState.putSerializable(SUGAR_TIME_CALENDAR_KEY, mSugarTimeCalendar);
        outState.putSerializable(MEAL_TIME_CALENDAR_KEY, mMealTimeCalendar);
        outState.putSerializable(INSULIN_TIME_CALENDAR_KEY, mInsulinTimeCalendar);
    }

    private boolean isInputValid() {
        boolean isValid  = true;
        EditText viewToFocus = null;
        if (mSugarLevel.getText().toString().isEmpty()) {
            viewToFocus = mSugarLevel;
        }
        else if (mSugarTime.getText().toString().isEmpty()) {
            viewToFocus = mSugarTime;
        }
        else if (mMeal.getText().toString().isEmpty()) {
            viewToFocus = mMeal;
        }
        else if (mMealTime.getText().toString().isEmpty()) {
            viewToFocus = mMealTime;
        }
        else if (mInsulinTime.getText().toString().isEmpty()) {
            viewToFocus = mInsulinTime;
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
        data.setSugarLevelTime(new Timestamp(mSugarTimeCalendar.getTimeInMillis()));
        data.setMeal(mMeal.getText().toString());
        data.setMealTime(new Timestamp(mMealTimeCalendar.getTimeInMillis()));
        data.setInsulinDosage(Float.valueOf(mInsulinDosage.getText().toString()));
        data.setInsulinTime(new Timestamp(mInsulinTimeCalendar.getTimeInMillis()));
        data.setMoodLevel(mMoodNumberPicker.getValue());
        data.setStressLevel(mStressNumberPicker.getValue());
        data.setEnergyLevel(mEnergyNumberPicker.getValue());
        data.setSugarLevelWho(mSugarLevelWho.getText().toString());
        data.setSugarLevelWhere(mSugarLevelWhere.getText().toString());
        data.setFeelings(mFeelings.getText().toString());
        return data;
    }

}
