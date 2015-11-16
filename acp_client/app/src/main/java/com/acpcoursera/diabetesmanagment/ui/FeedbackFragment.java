package com.acpcoursera.diabetesmanagment.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
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

public class FeedbackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FeedbackFragment.class.getSimpleName();

    private static final int FOLLOWINGS_LOADER_ID = 0;
    private static final int CHECK_IN_DATA_LOADER_ID = 1;
    private static final String CHECK_IN_DATA_LOADER_USERNAME = "loader_username";

    FollowingsAdapter mFollowingsAdapter;
    Spinner mFollowings;
    CheckInDataAdapter mCheckInDataAdapter;
    ListView mCheckInDataList;

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
                }
                else {
                    loaderManager
                            .restartLoader(CHECK_IN_DATA_LOADER_ID, loaderArgs, FeedbackFragment.this);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

}
