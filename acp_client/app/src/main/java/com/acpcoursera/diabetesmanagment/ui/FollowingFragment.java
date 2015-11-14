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
import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class FollowingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FollowingFragment.class.getSimpleName();

    private static final int LOADER_ID = 0;
    public static final int REQUEST_FOLLOW = 2;

    private CursorAdapter mAdapter;
    private ListView mFollowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_following, container, false);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        mAdapter = new FollowingListAdapter(getActivity(), null, 0);
        mFollowing = (ListView) rootView.findViewById(R.id.following_list_view);
        mFollowing.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO do a smarter sync
        Bundle data = new Bundle();
        data.putString("table", DmContract.Following.CONTENT_TYPE_ID);
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
                        DmContract.Following.buildFollowingsUri(),
                        new String[]
                                {
                                        DmContract.Following._ID,
                                        DmContract.Following.FOLLOWING_NAME,
                                        DmContract.Following.FOLLOWING_FULL_NAME,
                                        DmContract.Following.PENDING,
                                        DmContract.Following.INVITE
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

    private class FollowingListAdapter extends CursorAdapter {

        public FollowingListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.following_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView idView = (TextView) view.findViewById(R.id.following_id);
            ImageView iconView = (ImageView) view.findViewById(R.id.following_icon);
            ImageView statusView = (ImageView) view.findViewById(R.id.following_status);
            TextView fullNameView = (TextView) view.findViewById(R.id.following_full_name);
            TextView usernameView = (TextView) view.findViewById(R.id.following_username);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following._ID));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_FULL_NAME));
            int pending = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following.PENDING));
            int isInvite = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following.INVITE));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_NAME));

//            Log.d(TAG, dumpCursorToString(cursor));

            idView.setText(Integer.toString(id));
            fullNameView.setText(fullName);
            usernameView.setText(username);

            iconView.setImageResource(R.drawable.ic_teen);
            if (isInvite != 0) {
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
