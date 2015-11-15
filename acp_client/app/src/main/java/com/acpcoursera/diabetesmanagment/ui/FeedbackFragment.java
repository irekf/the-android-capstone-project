package com.acpcoursera.diabetesmanagment.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class FeedbackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FeedbackFragment.class.getSimpleName();

    private static final int FOLLOWINGS_LOADER_ID = 0;

    MyCursorAdapter mAdapter;
    Spinner mFollowings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_feedback, container, false);

        getLoaderManager().initLoader(FOLLOWINGS_LOADER_ID, null, this);

        mAdapter = new MyCursorAdapter(getActivity(), R.layout.following_drop_down_item, null, 0);
        mFollowings = (Spinner) rootView.findViewById(R.id.following_spinner);
        mFollowings.setAdapter(mAdapter);

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
                                        DmContract.Following.FOLLOWING_FULL_NAME,
                                },
                        DmContract.Following.PENDING + " = 0 AND "
                                + DmContract.Following.INVITE + " = 0",
                        null,
                        null
                );
            default:
                break;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "On load finished");
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "On load reset");
        mAdapter.changeCursor(null);
    }

    private class MyCursorAdapter extends ResourceCursorAdapter {

        public MyCursorAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView idView = (TextView) view.findViewById(R.id.following_drop_down_id);
            TextView fullNameView = (TextView) view.findViewById(R.id.following_drop_down_full_name);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following._ID));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_FULL_NAME));

            Log.d(TAG, "we got a full name: " + fullName);

            idView.setText(Integer.toString(id));
            fullNameView.setText(fullName);

        }
    };

}
