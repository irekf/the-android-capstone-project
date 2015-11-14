package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserSettings;
import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class FollowersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FollowersFragment.class.getSimpleName();

    private static final int TEST_LOADER = 0;

    public static final int REQUEST_INVITE = 1;

    private SimpleCursorAdapter mAdapter;
    private ListView mFollowers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_followers, container, false);

        getLoaderManager().initLoader(TEST_LOADER, null, this);

        mFollowers = (ListView) rootView.findViewById(R.id.followers_list_view);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.follower_item,
                null,
                new String[]
                        {
                                DmContract.Followers._ID,
                                DmContract.Followers.FOLLOWER_NAME,
                                DmContract.Followers.ACCEPTED
                        },
                new int[]
                        {
                                R.id.follower_id,
                                R.id.follower_name,
                                R.id.follower_accepted
                        },
                0
        );

        mFollowers.setAdapter(mAdapter);

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
                        DmContract.Followers.buildFollowersUri(),
                        new String[]
                                {
                                        DmContract.Followers._ID,
                                        DmContract.Followers.FOLLOWER_NAME,
                                        DmContract.Followers.ACCEPTED
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
        inflater.inflate(R.menu.menu_invite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_invite:
                Intent intent = new Intent(getActivity(), UserListActivity.class);
                startActivityForResult(intent, REQUEST_INVITE);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == Activity.RESULT_OK) {
                String username = data.getStringExtra(UserListActivity.EXTRA_USERNAME);
                UserSettings setting = data.getParcelableExtra(UserListActivity.EXTRA_USER_SETTINGS);
                Log.d(TAG, "username = " + username + ", " + setting.toString());
            }
        }
    }

}
