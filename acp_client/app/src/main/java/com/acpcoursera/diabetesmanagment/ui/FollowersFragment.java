package com.acpcoursera.diabetesmanagment.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acpcoursera.diabetesmanagment.R;
import com.acpcoursera.diabetesmanagment.model.UserSettings;
import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class FollowersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FollowersFragment.class.getSimpleName();

    private static final int LOADER_ID = 0;
    public static final int REQUEST_INVITE = 1;

    private CursorAdapter mAdapter;
    private ListView mFollowers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_followers, container, false);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        mAdapter = new FollowersListAdapter(getActivity(), null, 0);
        mFollowers = (ListView) rootView.findViewById(R.id.followers_list_view);
        mFollowers.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO do a smarter sync
        Bundle data = new Bundle();
        data.putString("table", DmContract.Followers.CONTENT_TYPE_ID);
        data.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(new Account(AuthActivity.ACCOUNT, AuthActivity.ACCOUNT_TYPE),
                AuthActivity.AUTHORITY, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        DmContract.Followers.buildFollowersUri(),
                        new String[]
                                {
                                        DmContract.Followers._ID,
                                        DmContract.Followers.FOLLOWER_NAME,
                                        DmContract.Followers.FOLLOWER_FULL_NAME,
                                        DmContract.Followers.TEEN,
                                        DmContract.Followers.ACCEPTED,
                                        DmContract.Followers.PENDING
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

    private class FollowersListAdapter extends CursorAdapter {

        public FollowersListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.follower_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView idView = (TextView) view.findViewById(R.id.follower_id);
            ImageView iconView = (ImageView) view.findViewById(R.id.follower_icon);
            ImageView statusView = (ImageView) view.findViewById(R.id.follower_status);
            TextView fullNameView = (TextView) view.findViewById(R.id.follower_full_name);
            TextView usernameView = (TextView) view.findViewById(R.id.follower_username);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Followers._ID));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Followers.FOLLOWER_FULL_NAME));
            int isTeen = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Followers.TEEN));
            int accepted = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Followers.ACCEPTED));
            int pending = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Followers.PENDING));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Followers.FOLLOWER_NAME));

//            Log.d(TAG, dumpCursorToString(cursor));

            idView.setText(Integer.toString(id));
            fullNameView.setText(fullName);
            usernameView.setText(username);

            if (isTeen != 0) {
                iconView.setImageResource(R.drawable.ic_teen);
            }
            else {
                iconView.setImageResource(R.drawable.ic_follower);
            }

            if (accepted == 0) {
                statusView.setImageResource(R.drawable.ic_new_request);
            }
            else if (pending != 0) {
                statusView.setImageResource(R.drawable.ic_request_sent);
            }
            else {
                statusView.setImageResource(0);
            }

        }

    }

}
