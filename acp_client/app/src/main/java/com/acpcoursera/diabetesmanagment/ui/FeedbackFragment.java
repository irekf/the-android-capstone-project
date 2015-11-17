package com.acpcoursera.diabetesmanagment.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.provider.DmContract;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class FeedbackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnChartGestureListener, OnChartValueSelectedListener {

    private static String TAG = FeedbackFragment.class.getSimpleName();

    private static final int FOLLOWINGS_LOADER_ID = 0;
    private static final int CHECK_IN_DATA_LOADER_ID = 1;
    private static final String CHECK_IN_DATA_LOADER_USERNAME = "loader_username";

    FollowingsAdapter mFollowingsAdapter;
    Spinner mFollowings;
    CheckInDataAdapter mCheckInDataAdapter;
    ListView mCheckInDataList;

    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_feedback, container, false);

        getLoaderManager().initLoader(FOLLOWINGS_LOADER_ID, null, this);

        mFollowingsAdapter = new FollowingsAdapter(getActivity(), R.layout.following_drop_down_item, null, 0);
        mFollowings = (Spinner) rootView.findViewById(R.id.following_spinner);
        mFollowings.setAdapter(mFollowingsAdapter);
        mFollowings.setSelection(0);

        mCheckInDataAdapter = new CheckInDataAdapter(getActivity(), null, 0);
        mCheckInDataList = (ListView) rootView.findViewById(R.id.check_in_data_list_view);
        mCheckInDataList.setAdapter(mCheckInDataAdapter);

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initChart(rootView);
        setDummyData(50, 100);

        return rootView;
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
                                        DmContract.CheckInData.INSULIN_DOSAGE,
                                },
                        DmContract.CheckInData.USERNAME + " = ? ",
                        new String[] { username },
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
                mCheckInDataAdapter.changeCursor(data);
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
                mCheckInDataAdapter.changeCursor(null);
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

        }
    };

    private class CheckInDataAdapter extends CursorAdapter {

        public CheckInDataAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.check_in_data_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView idView = (TextView) view.findViewById(R.id.check_in_data_id);
            TextView bloodSugarLevelView = (TextView) view.findViewById(R.id.check_in_blood_sugar);
            TextView insulinDosageView = (TextView) view.findViewById(R.id.check_in_insulin);

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.CheckInData._ID));
            int bloodSugarLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.CheckInData.SUGAR_LEVEL));
            int insulinDosage = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.CheckInData.INSULIN_DOSAGE));

            idView.setText(Integer.toString(id));
            bloodSugarLevelView.setText(Integer.toString(bloodSugarLevel));
            insulinDosageView.setText(Integer.toString(insulinDosage));

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

    // thanks MPAndroidChart for this example
    private void setDummyData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

}
