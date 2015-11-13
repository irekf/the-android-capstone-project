package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class FollowingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FollowingFragment.class.getSimpleName();

    private static final int TEST_LOADER = 0;

    public static final int REQUEST_FOLLOW = 2;

    private SimpleCursorAdapter mAdapter;
    private ListView mFollowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_following, container, false);

        getLoaderManager().initLoader(TEST_LOADER, null, this);

        mFollowing = (ListView) rootView.findViewById(R.id.following_list_view);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.following_item,
                null,
                new String[]
                        {
                                DmContract.Following._ID,
                                DmContract.Following.FOLLOWING_NAME,
                                DmContract.Following.PENDING,
                                DmContract.Following.IS_INVITE
                        },
                new int[]
                        {
                                R.id.following_id,
                                R.id.following_name,
                                R.id.following_pending,
                                R.id.following_is_invite
                        },
                0
        );

        mFollowing.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case TEST_LOADER:
                return new CursorLoader(
                        getActivity(),
                        DmContract.Following.buildFollowingsUri(),
                        new String[]
                                {
                                        DmContract.Following._ID,
                                        DmContract.Following.FOLLOWING_NAME,
                                        DmContract.Following.PENDING,
                                        DmContract.Following.IS_INVITE
                                },
                        null,
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
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_follow, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_follow:
                Intent intent = new Intent(getActivity(), UserListActivity.class);
                startActivityForResult(intent, REQUEST_FOLLOW);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==REQUEST_FOLLOW) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

}
