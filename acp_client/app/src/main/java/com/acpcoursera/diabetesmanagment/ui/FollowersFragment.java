package com.acpcoursera.diabetesmanagment.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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
import com.acpcoursera.diabetesmanagment.service.NetOpsService;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class FollowersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FollowersFragment.class.getSimpleName();

    private static final int LOADER_ID = 0;
    public static final int REQUEST_INVITE = 1;

    private NetOpsReceiver mReceiver;

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
                String usernameToInvite = data.getStringExtra(UserListActivity.EXTRA_USERNAME);
                UserSettings settings = data.getParcelableExtra(UserListActivity.EXTRA_USER_SETTINGS);
                sendInviteRequest(usernameToInvite, settings);
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
            ImageView actionView = (ImageView) view.findViewById(R.id.follower_action);
            ImageView deleteView = (ImageView) view.findViewById(R.id.follower_delete);

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
                actionView.setImageResource(R.drawable.ic_accept);
            }
            else if (pending != 0) {
                statusView.setImageResource(R.drawable.ic_request_sent);
                actionView.setImageResource(0);
            }
            else {
                statusView.setImageResource(0);
                actionView.setImageResource(R.drawable.ic_edit);
            }

        }

    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (action.equals(NetOpsService.ACTION_INVITE)) {
                if (resultCode == NetOpsService.RC_OK) {
                    showToast(context, context.getString(R.string.success_invite));
                }
                else {
                    showToast(context, context.getString(R.string.error_invite) +
                            intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE));
                }
                ProgressDialogFragment.dismiss(getActivity());
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_INVITE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private void sendInviteRequest(String usernameToFollow, UserSettings settings) {
        ProgressDialogFragment.show(getActivity());
        Intent intent = new Intent(getActivity(), NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_INVITE);
        intent.putExtra(NetOpsService.ARG_USER_NAME, usernameToFollow);
        intent.putExtra(NetOpsService.ARG_USER_SETTINGS, settings);
        getActivity().startService(intent);
    }

}
