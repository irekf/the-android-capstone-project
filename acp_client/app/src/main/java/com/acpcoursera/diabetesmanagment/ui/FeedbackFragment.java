package com.acpcoursera.diabetesmanagment.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserSettings;
import com.acpcoursera.diabetesmanagment.provider.DmContract;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class FeedbackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnChartGestureListener, OnChartValueSelectedListener {

    private static String TAG = FeedbackFragment.class.getSimpleName();

    private static final int FOLLOWINGS_LOADER_ID = 0;
    private static final int CHECK_IN_DATA_LOADER_ID = 1;
    private static final int CHECK_IN_DETAILS_LOADER_ID = 2;

    private static final String CHECK_IN_DATA_LOADER_USERNAME = "loader_username";
    private static final String CHECK_IN_DETAILS_LOADER_ROW_ID = "details_loader_row_id";

    private FollowingsAdapter mFollowingsAdapter;
    private Spinner mFollowings;

    private LineChart mChart;
    private View mDetailsView;

    private UserSettings mPickedFollowingSettings;
    private static final String USER_SETTINGS_KEY = "user_settings_key";
    private int mPickedCheckInRowId;
    private static final String PICKED_CHECK_IN_ROW_KEY = "picked_check_in_row_key";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_feedback, container, false);

        getLoaderManager().initLoader(FOLLOWINGS_LOADER_ID, null, this);

        mDetailsView = rootView.findViewById(R.id.check_in_details_frame);
        if (savedInstanceState != null) {
            mPickedCheckInRowId = savedInstanceState.getInt(PICKED_CHECK_IN_ROW_KEY);
            mPickedFollowingSettings = savedInstanceState.getParcelable(USER_SETTINGS_KEY);
        }
        else {
            mPickedCheckInRowId = -1;
            mPickedFollowingSettings = new UserSettings(false, false);
        }

        mFollowingsAdapter = new FollowingsAdapter(getActivity(), R.layout.following_drop_down_item, null, 0);
        mFollowings = (Spinner) rootView.findViewById(R.id.following_spinner);
        mFollowings.setAdapter(mFollowingsAdapter);

        if (savedInstanceState == null) {
            mFollowings.setSelection(0);
        }

        mFollowings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout followingItem = (LinearLayout) view;
                TextView username
                        = (TextView) followingItem.findViewById(R.id.following_drop_down_username);
                Bundle loaderArgs = new Bundle();
                loaderArgs.putString(CHECK_IN_DATA_LOADER_USERNAME,
                        username.getText().toString());

                LoaderManager loaderManager = getLoaderManager();
                if (loaderManager.getLoader(CHECK_IN_DATA_LOADER_ID) == null) {
                    loaderManager
                            .initLoader(CHECK_IN_DATA_LOADER_ID, loaderArgs, FeedbackFragment.this);
                } else {
                    loaderManager
                            .restartLoader(CHECK_IN_DATA_LOADER_ID, loaderArgs, FeedbackFragment.this);
                }

                resetCheckInDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initChart(rootView);
//        if (mPickedCheckInRowId != -1) {
//            onValueSelected(new Entry(0, 0, mPickedCheckInRowId), 0, null);
//        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(USER_SETTINGS_KEY, mPickedFollowingSettings);
        outState.putInt(PICKED_CHECK_IN_ROW_KEY, mPickedCheckInRowId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case FOLLOWINGS_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        DmContract.Following.buildFollowingsUri(),
                        new String[]
                                {
                                        DmContract.Following._ID,
                                        DmContract.Following.FOLLOWING_NAME,
                                        DmContract.Following.FOLLOWING_FULL_NAME,
                                        DmContract.Followers.MAJOR_DATA,
                                        DmContract.Following.MINOR_DATE
                                },
                        DmContract.Following.PENDING + " = 0 AND "
                                + DmContract.Following.INVITE + " = 0",
                        null,
                        null
                );
            case CHECK_IN_DATA_LOADER_ID:
                String username = args.getString(CHECK_IN_DATA_LOADER_USERNAME);
                return new CursorLoader(
                        getActivity(),
                        DmContract.CheckInData.buildCheckInDataUri(),
                        new String[]
                                {
                                        DmContract.CheckInData._ID,
                                        DmContract.CheckInData.SUGAR_LEVEL,
                                        DmContract.CheckInData.SUGAR_LEVEL_TIME,
                                        DmContract.CheckInData.INSULIN_DOSAGE,
                                        DmContract.CheckInData.INSULIN_TIME,
                                        DmContract.CheckInData.CHECK_IN_TIME
                                },
                        DmContract.CheckInData.USERNAME + " = ? ",
                        new String[] { username },
                        null
                );
            case CHECK_IN_DETAILS_LOADER_ID:
                int rowId = args.getInt(CHECK_IN_DETAILS_LOADER_ROW_ID);
                String rowIdString = Integer.toString(rowId);
                return new CursorLoader(
                        getActivity(),
                        DmContract.CheckInData.buildCheckInDataUri(),
                        new String[]
                                {
                                        DmContract.CheckInData.SUGAR_LEVEL,
                                        DmContract.CheckInData.SUGAR_LEVEL_TIME,
                                        DmContract.CheckInData.INSULIN_DOSAGE,
                                        DmContract.CheckInData.INSULIN_TIME,
                                        DmContract.CheckInData.MEAL,
                                        DmContract.CheckInData.MEAL_TIME,
                                        DmContract.CheckInData.MOOD_LEVEL,
                                        DmContract.CheckInData.STRESS_LEVEL,
                                        DmContract.CheckInData.ENERGY_LEVEL,
                                        DmContract.CheckInData.SUGAR_LEVEL_WHO,
                                        DmContract.CheckInData.SUGAR_LEVEL_WHERE,
                                        DmContract.CheckInData.FEELINGS,
                                        DmContract.CheckInData.CHECK_IN_TIME
                                },
                        DmContract.CheckInData._ID + " = ? ",
                        new String[] { rowIdString },
                        null
                );
            default:
                break;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case FOLLOWINGS_LOADER_ID:
                mFollowingsAdapter.changeCursor(data);
                break;
            case CHECK_IN_DATA_LOADER_ID:
                loadDataToChart(data);
                break;
            case CHECK_IN_DETAILS_LOADER_ID:
                loadCheckInDetails(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FOLLOWINGS_LOADER_ID:
                mFollowingsAdapter.changeCursor(null);
                break;
            case CHECK_IN_DATA_LOADER_ID:
                loadDataToChart(null);
                break;
            case CHECK_IN_DETAILS_LOADER_ID:
                resetCheckInDetails();
                break;
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e.getData() != null) {
            Integer checkInRowId = (Integer) e.getData();
            mPickedCheckInRowId = checkInRowId;

            Bundle loaderArgs = new Bundle();
            loaderArgs.putInt(CHECK_IN_DETAILS_LOADER_ROW_ID, checkInRowId);

            LoaderManager loaderManager = getLoaderManager();
            if (loaderManager.getLoader(CHECK_IN_DETAILS_LOADER_ID) == null) {
                loaderManager
                        .initLoader(CHECK_IN_DETAILS_LOADER_ID, loaderArgs, FeedbackFragment.this);
            } else {
                loaderManager
                        .restartLoader(CHECK_IN_DETAILS_LOADER_ID, loaderArgs, FeedbackFragment.this);
            }

        }
    }

    @Override
    public void onNothingSelected() {

    }

    private class FollowingsAdapter extends ResourceCursorAdapter {

        public FollowingsAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView idView = (TextView) view.findViewById(R.id.following_drop_down_id);
            TextView usernameView = (TextView) view.findViewById(R.id.following_drop_down_username);
            TextView fullNameView = (TextView) view.findViewById(R.id.following_drop_down_full_name);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following._ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_NAME));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_FULL_NAME));

            idView.setText(Integer.toString(id));
            usernameView.setText(username);
            fullNameView.setText(fullName);

            int majorData = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following.MAJOR_DATA));
            int minorData = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following.MINOR_DATE));

            mPickedFollowingSettings.setMajorData(majorData != 0);
            mPickedFollowingSettings.setMinorData(minorData != 0);

        }
    };

    private void initChart(View view) {
        mChart = (LineChart) view.findViewById(R.id.feedback_chart);
        mChart.setOnChartGestureListener(FeedbackFragment.this);
        mChart.setOnChartValueSelectedListener(FeedbackFragment.this);

        mChart.setDescription("");
        mChart.setNoDataTextDescription(getString(R.string.no_feedback_data));

        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
    }

    private void loadDataToChart(Cursor data) {

        if (data == null || data.getCount() == 0) {
            mChart.clear();
            return;
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        ArrayList<Long> timeValues = new ArrayList<>();

        ArrayList<Entry> sugarLevelSet = new ArrayList<>();
        ArrayList<Entry> insulinDosageSet = new ArrayList<>();
        ArrayList<Entry> checkInId = new ArrayList<>();

        for (int i = 0; data.moveToNext(); i += 3) {
            Integer id = data.getInt(
                    data.getColumnIndexOrThrow(DmContract.CheckInData._ID));
            String checkInTime = data.getString(
                    data.getColumnIndexOrThrow(DmContract.CheckInData.CHECK_IN_TIME));
            String sugarTime = data.getString(
                    data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL_TIME));
            String insulinTime = data.getString(
                    data.getColumnIndexOrThrow(DmContract.CheckInData.INSULIN_TIME));
            int sugarLevel = data.getInt(
                    data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL));
            int insulinDosage = data.getInt(
                    data.getColumnIndexOrThrow(DmContract.CheckInData.INSULIN_DOSAGE));

            try {
                timeValues.add(timeFormat.parse(checkInTime).getTime());
                timeValues.add(timeFormat.parse(sugarTime).getTime());
                timeValues.add(timeFormat.parse(insulinTime).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            sugarLevelSet.add(new Entry(sugarLevel, i));
            insulinDosageSet.add(new Entry(insulinDosage, i + 1));
            checkInId.add(new Entry(0, i + 2, id));
        }

        Long minTimeValue = Collections.min(timeValues);

        ArrayList<String> timeValuesString = new ArrayList<>();
        for (int i = 0; i < timeValues.size(); i++) {
            timeValues.set(i, timeValues.get(i) % minTimeValue);
            timeValuesString.add(Long.toString(timeValues.get(i) % minTimeValue));
        }

        LineDataSet set1 = new LineDataSet(sugarLevelSet, "Sugar Level");
        LineDataSet set2 = new LineDataSet(insulinDosageSet, "Insulin Dosage");
        LineDataSet set3 = new LineDataSet(checkInId, "Check-In Entries");

        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);

        set2.enableDashedLine(10f, 5f, 0f);
        set2.enableDashedHighlightLine(10f, 5f, 0f);
        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLACK);
        set2.setLineWidth(1f);
        set2.setCircleSize(3f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.BLACK);

        set3.enableDashedLine(10f, 5f, 0f);
        set3.enableDashedHighlightLine(10f, 5f, 0f);
        set3.setColor(Color.YELLOW);
        set3.setCircleColor(Color.YELLOW);
        set3.setLineWidth(1f);
        set3.setCircleSize(10f);
        set3.setDrawCircleHole(false);
        set3.setValueTextSize(9f);
        set3.setFillAlpha(65);
        set3.setFillColor(Color.BLACK);
        set3.setDrawValues(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);

        LineData lineData = new LineData(timeValuesString, dataSets);
        mChart.setData(lineData);
        mChart.getXAxis().setDrawLabels(false);
        mChart.invalidate();
    }

    private void loadCheckInDetails(Cursor data) {

        if (!data.moveToNext()) {
            resetCheckInDetails();
            return;
        }

        // major data
        TextView checkInTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_check_in_date);
        TextView sugarView = (TextView) mDetailsView.findViewById(R.id.feedback_sugar);
        TextView sugarTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_sugar_time);
        TextView insulinView = (TextView) mDetailsView.findViewById(R.id.feedback_insulin);
        TextView insulinTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_insulin_time);
        TextView mealView = (TextView) mDetailsView.findViewById(R.id.feedback_meal);
        TextView mealTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_meal_time);

        // minor data
        TextView moodView = (TextView) mDetailsView.findViewById(R.id.feedback_mood);
        TextView stressView = (TextView) mDetailsView.findViewById(R.id.feedback_stress);
        TextView energyView = (TextView) mDetailsView.findViewById(R.id.feedback_energy);
        TextView whoView = (TextView) mDetailsView.findViewById(R.id.feedback_who);
        TextView whereView = (TextView) mDetailsView.findViewById(R.id.feedback_where);
        TextView feelingsView = (TextView) mDetailsView.findViewById(R.id.feedback_feelings);

        String checkInTime = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.CHECK_IN_TIME));
        int sugar = data.getInt(data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL));
        String sugarTime = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL_TIME));
        int insulin = data.getInt(data.getColumnIndexOrThrow(DmContract.CheckInData.INSULIN_DOSAGE));
        String insulinTime = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.INSULIN_TIME));
        String meal = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.MEAL));
        String mealTime = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.MEAL_TIME));

        int mood = data.getInt(data.getColumnIndexOrThrow(DmContract.CheckInData.MOOD_LEVEL));
        int stress = data.getInt(data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL));
        int energy = data.getInt(data.getColumnIndexOrThrow(DmContract.CheckInData.ENERGY_LEVEL));
        String who = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL_WHO));
        String where = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL_WHERE));
        String feelings = data.getString(data.getColumnIndexOrThrow(DmContract.CheckInData.FEELINGS));

        if (mPickedFollowingSettings.isMajorData()) {
            checkInTimeView.setText(checkInTime);
            sugarView.setText(Integer.toString(sugar));
            sugarTimeView.setText(sugarTime);
            insulinView.setText(Integer.toString(insulin));
            insulinTimeView.setText(insulinTime);
            mealView.setText(meal);
            mealTimeView.setText(mealTime);
        }
        else {
            checkInTimeView.setText(checkInTime);
            sugarView.setText("n/a");
            sugarTimeView.setText("n/a");
            insulinView.setText("n/a");
            insulinTimeView.setText("n/a");
            mealView.setText("n/a");
            mealTimeView.setText("n/a");
        }

        if (mPickedFollowingSettings.isMinorData()) {
            moodView.setText(Integer.toString(mood));
            stressView.setText(Integer.toString(stress));
            energyView.setText(Integer.toString(energy));
            whoView.setText(who);
            whereView.setText(where);
            feelingsView.setText(feelings);
        }
        else {
            moodView.setText("n/a");
            stressView.setText("n/a");
            energyView.setText("n/a");
            whoView.setText("n/a");
            whereView.setText("n/a");
            feelingsView.setText("n/a");
        }
    }

    private void resetCheckInDetails() {
        // major data
        TextView checkInTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_check_in_date);
        TextView sugarView = (TextView) mDetailsView.findViewById(R.id.feedback_sugar);
        TextView sugarTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_sugar_time);
        TextView insulinView = (TextView) mDetailsView.findViewById(R.id.feedback_insulin);
        TextView insulinTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_insulin_time);
        TextView mealView = (TextView) mDetailsView.findViewById(R.id.feedback_meal);
        TextView mealTimeView = (TextView) mDetailsView.findViewById(R.id.feedback_meal_time);

        // minor data
        TextView moodView = (TextView) mDetailsView.findViewById(R.id.feedback_mood);
        TextView stressView = (TextView) mDetailsView.findViewById(R.id.feedback_stress);
        TextView energyView = (TextView) mDetailsView.findViewById(R.id.feedback_energy);
        TextView whoView = (TextView) mDetailsView.findViewById(R.id.feedback_who);
        TextView whereView = (TextView) mDetailsView.findViewById(R.id.feedback_where);
        TextView feelingsView = (TextView) mDetailsView.findViewById(R.id.feedback_feelings);

        checkInTimeView.setText("–");
        sugarView.setText("–");
        sugarTimeView.setText("–");
        insulinView.setText("–");
        insulinTimeView.setText("–");
        mealView.setText("–");
        mealTimeView.setText("–");

        moodView.setText("–");
        stressView.setText("–");
        energyView.setText("–");
        whoView.setText("–");
        whereView.setText("–");
        feelingsView.setText("–");
    }

}
