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
import com.acpcoursera.diabetesmanagment.util.MiscUtils;

import static com.acpcoursera.diabetesmanagment.util.MiscUtils.showToast;

public class FollowingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FollowingFragment.class.getSimpleName();

    private static final int LOADER_ID = 0;
    public static final int REQUEST_FOLLOW = 2;

    private NetOpsReceiver mReceiver;

    private CursorAdapter mAdapter;
    private ListView mFollowing;

    private String mUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_following, container, false);

        mUsername = MiscUtils.readUsername(getActivity());
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
                        DmContract.Following.FOLLOWING_NAME + " != ?",
                        new String[] { mUsername },
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
                intent.putExtra(UserListActivity.ARG_TEEN_ONLY, true);
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
                String usernameToFollow = data.getStringExtra(UserListActivity.EXTRA_USERNAME);
                UserSettings settings = data.getParcelableExtra(UserListActivity.EXTRA_USER_SETTINGS);
                sendFollowRequest(usernameToFollow, settings);
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
            ImageView actionView = (ImageView) view.findViewById(R.id.following_action);
            ImageView deleteView = (ImageView) view.findViewById(R.id.following_delete);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following._ID));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_FULL_NAME));
            int pending = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following.PENDING));
            final int isInvite = cursor.getInt(cursor.getColumnIndexOrThrow(DmContract.Following.INVITE));
            final String username = cursor.getString(cursor.getColumnIndexOrThrow(DmContract.Following.FOLLOWING_NAME));

//            Log.d(TAG, dumpCursorToString(cursor));

            idView.setText(Integer.toString(id));
            fullNameView.setText(fullName);
            usernameView.setText(username);

            iconView.setImageResource(R.drawable.ic_teen);
            if (isInvite != 0) {
                statusView.setImageResource(R.drawable.ic_new_request);
                actionView.setImageResource(R.drawable.ic_accept);
            }
            else if (pending != 0) {
                statusView.setImageResource(R.drawable.ic_request_sent);
                actionView.setImageResource(0);
            }
            else {
                statusView.setImageResource(0);
                actionView.setImageResource(0);
            }

            // listeners
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInvite != 0) {
                        acceptInvite(username);
                    }
                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUser(username);
                }
            });

        }

    }

    private class NetOpsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            int resultCode = intent.getIntExtra(NetOpsService.RESULT_CODE, NetOpsService.RC_MISSING);
            if (resultCode == NetOpsService.RC_OK) {
                if (action.equals(NetOpsService.ACTION_FOLLOW)) {
                    showToast(context, context.getString(R.string.success_follow));
                } else if (action.equals(NetOpsService.ACTION_ACCEPT)) {
                    showToast(context, context.getString(R.string.request_accepted));
                } else if (action.equals(NetOpsService.ACTION_DELETE)) {
                    showToast(context, context.getString(R.string.following_deleted));
                }
            }
            else {
                showToast(context, context.getString(R.string.error_follower_request) +
                        intent.getStringExtra(NetOpsService.EXTRA_ERROR_MESSAGE));
            }

            ProgressDialogFragment.dismiss(getActivity());

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new NetOpsReceiver();
        IntentFilter filter = new IntentFilter(TAG);
        filter.addAction(NetOpsService.ACTION_FOLLOW);
        filter.addAction(NetOpsService.ACTION_ACCEPT);
        filter.addAction(NetOpsService.ACTION_DELETE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private void sendFollowRequest(String usernameToFollow, UserSettings settings) {
        ProgressDialogFragment.show(getActivity());
        Intent intent = new Intent(getActivity(), NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_FOLLOW);
        intent.putExtra(NetOpsService.ARG_USER_NAME, usernameToFollow);
        intent.putExtra(NetOpsService.ARG_USER_SETTINGS, settings);
        getActivity().startService(intent);
    }

    private void acceptInvite(String username) {
        ProgressDialogFragment.show(getActivity());
        Intent intent = new Intent(getActivity(), NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_ACCEPT);
        intent.putExtra(NetOpsService.ARG_USER_NAME, username);
        intent.putExtra(NetOpsService.ARG_IS_INVITE, true);
        getActivity().startService(intent);
    }

    private void deleteUser(String username) {
        ProgressDialogFragment.show(getActivity());
        Intent intent = new Intent(getActivity(), NetOpsService.class);
        intent.setAction(NetOpsService.ACTION_DELETE);
        intent.putExtra(NetOpsService.ARG_USER_NAME, username);
        intent.putExtra(NetOpsService.ARG_IS_FOLLOWER, false);
        getActivity().startService(intent);
    }

}
