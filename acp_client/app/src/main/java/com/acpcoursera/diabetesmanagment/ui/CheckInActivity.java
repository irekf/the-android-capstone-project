package com.acpcoursera.diabetesmanagment.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.acpcoursera.diabetesmanagment.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CheckInActivity extends AppCompatActivity {

    private static String TAG = CheckInActivity.class.getSimpleName();

    private static final String MOOD_LEVEL_KEY = "mood_level_key";
    private static final String STRESS_LEVEL_KEY = "stress_level_key";
    private static final String ENERGY_LEVEL_KEY = "energy_level_key";

    private NumberPicker mMoodNumberPicker;
    private NumberPicker mStressNumberPicker;
    private NumberPicker mEnergyNumberPicker;

    private EditText mMeasurementTime;
    private EditText mMealTime;
    private EditText mInsulinAdministrationTime;

    private EditText mCurrentTimeAndDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

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
                new TimePickerDialog(CheckInActivity.this, timeListener,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true).show();
            }
        };

        EditText.OnClickListener timeAndDateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentTimeAndDateField = (EditText) v;
                new DatePickerDialog(CheckInActivity.this, dateListener,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };

        mMeasurementTime = (EditText) findViewById(R.id.measurement_time_edit_text);
        mMeasurementTime.setOnClickListener(timeAndDateListener);
        mMealTime = (EditText) findViewById(R.id.meal_time_edit_text);
        mMealTime.setOnClickListener(timeAndDateListener);
        mInsulinAdministrationTime = (EditText) findViewById(R.id.insulin_administered_edit_text);
        mInsulinAdministrationTime.setOnClickListener(timeAndDateListener);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MOOD_LEVEL_KEY, mMoodNumberPicker.getValue());
        outState.putInt(STRESS_LEVEL_KEY, mStressNumberPicker.getValue());
        outState.putInt(ENERGY_LEVEL_KEY, mEnergyNumberPicker.getValue());
    }

}
